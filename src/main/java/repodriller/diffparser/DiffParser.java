package repodriller.diffparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import customDiff.CustomDiff;
import customDiff.SPLdomain.ProductRelease;
import customDiff.SPLdomain.SourceCodeFile;
import customDiff.SPLdomain.VariationPoint;
import customDiff.blame.BlamedLine;

public class DiffParser {

	private List<DiffBlock> diffBlocks;
	private List<CustomDiffBlock> customDiffBlocks; //by variation point
	private String fullDiff;
	private ProductRelease pr;
	private String path; //pa path
	private Modification modification;
	public DiffParser(Modification mod, ProductRelease pr){//(String fullDiff, ProductRelease pr, String path) {
		this.fullDiff = mod.getDiff();//fullDiff;
		diffBlocks = new ArrayList<DiffBlock>();
		this.pr  =pr;
		this.path = mod.getNewPath();// path;
		this.modification = mod;
		extractDiffBlocks();
		
		extractCustomDiffBlocks();/** Parse blocks for customDiff*/
		
	}

	
	private void extractDiffBlocks() {
		String[] lines = fullDiff.replace("\r", "").split("\n");
		String[] linesNoHeader = Arrays.copyOfRange(lines, 4, lines.length);

		StringBuilder currentDiff = new StringBuilder();
		boolean currentInADiff = false;
		
		for(int i = 0; i < linesNoHeader.length; i++) {
			String currentLine = linesNoHeader[i];
			if(currentLine.startsWith("@@ -") && !currentInADiff) {
				currentInADiff = true;
			}
			else if(currentLine.startsWith("@@ -") && currentInADiff) {
				diffBlocks.add(new DiffBlock(currentDiff.toString()));
				currentDiff = new StringBuilder();
				currentInADiff = false;
				i--;
			}

			if(currentInADiff) currentDiff.append(currentLine + "\n");
		}
		diffBlocks.add(new DiffBlock(currentDiff.toString()));

	}
	
	/** Leticia Montalvillo **/
	public void extractCustomDiffBlocks(){
		customDiffBlocks = new ArrayList<CustomDiffBlock>();
		int numberOfBlock= getBlocks().size(); //how many blocks in the "normal" diff	
		System.out.println("Number of blocks to parse are: "+numberOfBlock);
		
		for(int i=0; i < numberOfBlock; i++){
			customDiffBlocks. addAll (parseBlockToExtractCustomDiffBlocks(getBlocks().get(i)));
		}
	}
	
	
	
	private ArrayList<CustomDiffBlock> parseBlockToExtractCustomDiffBlocks(DiffBlock diffBlock) {
		System.out.println("Parsing diff block diff \n"+diffBlock.getDiffBlock());
		ArrayList<CustomDiffBlock> customDiffBlocks = new ArrayList<CustomDiffBlock>();
		
		String header = diffBlock.getLines()[0];//("@@ -(\\d*),(\\d*) \\+(\\d*),(\\d*) @@.*");
		
		int lineCounter=0;
		int oldfileLineNumber = diffBlock.getD1(); //System.out.println("d1: "+oldfileLineNumber);
		int newFileLineNumber = diffBlock.getD3(); //System.out.println("d3: "+newFileLineNumber);
		
		String[] lines = diffBlock.getLines();
		int startLine=0;
		boolean anyVP=false;
		
		for (String line: lines ){
			if(line.startsWith("-")) oldfileLineNumber++;
			else if(line.startsWith("+")) newFileLineNumber++;
			else {//for context lines	
				oldfileLineNumber++;
				newFileLineNumber++;
			}
			
			if(line.contains(CustomDiff.annotationPatternBeginning)){
				anyVP=true;
				customDiffBlocks.add(createCustomDiffBlock(diffBlock,startLine, lineCounter-1, true,oldfileLineNumber,newFileLineNumber));
				startLine = lineCounter;
			}
			lineCounter ++;
		}
		
		if (anyVP==false){//if the diff did not contain any variation point in it
			customDiffBlocks.add(createCustomDiffBlock(diffBlock,startLine, lines.length-1, false,oldfileLineNumber,newFileLineNumber));
		}
		return customDiffBlocks;
	}
	
	/** Custom Diff: per variation point. With expression in the header & with authors name in changes**/
	private CustomDiffBlock createCustomDiffBlock(DiffBlock diffBlock, int startLine, int endline, 
			boolean fixHeader,int d1, int d3){
		System.out.println("New chunk! From line "+startLine+" to line:"+ endline);
		ArrayList<String> newDiff = new ArrayList<>();
		String[] originalDiff = diffBlock.getLines();
		
		if (fixHeader) {//TODO Fix the header lines!
			//newDiff.add(originalDiff[0]);//add the header.
			newDiff.add("@@-"+d1+",d2"+" +"+d3+",d4");//add the header.
			
		}  
		int newFilelineCounter = startLine+diffBlock.getD3()-2;//TODO startLine+diffBlock.getD3()-1
		String strblame;
		for(int i=startLine; i<=endline; i++){
			strblame= "";
			if(originalDiff[i].startsWith("+")) {// introduce THE blame for that line, only for added lines
				List<BlamedLine> blames = modification.getBlameLines();
				String author = blames.get(newFilelineCounter).getAuthor();int line = blames.get(newFilelineCounter).getLineNumber();
				String commit = blames.get(newFilelineCounter).getCommit();String lineAdded = blames.get(newFilelineCounter).getLine();
				strblame = "//Authored by: "+author+" " + "lineNumber:"+line+"in commit:"+commit+ "line added:"+lineAdded;
				
			}
			newDiff.add(originalDiff[i]+strblame);//add line to the customDiffBlock
			
			if (!originalDiff[i].startsWith("-")) newFilelineCounter++;
		}
		
		//add initial context lines. TODO 
		//add final context lines. TODO 

		if( diffBlockHasModifications(newDiff)==false)
			return null; //there are no modifications in this chunk - it was part of the context
		
		
		SourceCodeFile paModified = customDiff.utils.FileUtils.getProductAssetByFilePath(path, pr);
		SourceCodeFile coreAsset = customDiff.utils.FileUtils.getCoreAssetByProductAssetPath( path,  pr );
		VariationPoint vp = customDiff.utils.FeatureAnalysisUtils.getVariationPointOfChangedAssetLine
											(paModified.getRelativePath(), pr, startLine+diffBlock.getD1());
		if(vp==null)
			vp = new VariationPoint(-1, "No Expression", 0, null);
		String header = getHeaderForVariationPoint(vp);
		
		newDiff.set(0, newDiff.get(0).concat(header));//add VP expression to header
		
	return new CustomDiffBlock(arrayListToString(newDiff),vp,header,paModified, coreAsset);

	}
	
	
	private String getHeaderForVariationPoint(VariationPoint vp) {
		if(vp==null) return "No VP";
		
		String header= vp.getExpression().replace("PV:IFCOND", "");
		header = header.replace(" ", "");header = header.replace("//", "");
		if(vp.getParentVP()!=null){
			header= header +" -> Nested into -> "+ getHeaderForVariationPoint(vp.getParentVP());
		}
		return header;
	}


	private boolean diffBlockHasModifications(ArrayList<String> newDiff) {
		Iterator<String> it = newDiff.iterator();
		String aux;
		
		while (it.hasNext()){
			aux = it.next();
			if(aux.startsWith("-") || aux.startsWith("+"))
				return true;
			
		}
		return false;
	}


	public List<DiffBlock> getBlocks() {//hunks
		return diffBlocks;
	}

	public String getFullDiff() {
		return fullDiff;
	}
	
	public List<CustomDiffBlock> getCustomDiffBlocks() {
		return customDiffBlocks;
	}
	public void setCustomDiffBlocks(List<CustomDiffBlock> customDiffBlocks) {
		this.customDiffBlocks = customDiffBlocks;
	}
	
	public String arrayListToString(ArrayList<String> array){
		String str="";
		for(int i=0; i< array.size();i++){
			str+=array.get(i)+"\n";
		}
		return str;
	}

}
