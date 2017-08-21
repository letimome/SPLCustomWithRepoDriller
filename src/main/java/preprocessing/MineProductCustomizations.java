package preprocessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import OldMain.AnalyzeFeatureDetail;

import org.repodriller.domain.Commit;
import org.repodriller.domain.DiffParser;
import org.repodriller.domain.Modification;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;
import org.repodriller.domain.DiffLine;

import SPLconcepts.CustomizationEffort;
/*
public class MineProductCustomizations implements CommitVisitor {

	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		for (Modification m : commit.getModifications()) {// POR CADA MODIFICACION DE UN COMMIT
			
			//Analyses all branches that are not master and contain "product" 
			if( (!commit.getBranches().contains(Main.coreAssetsBranchPatternName)) 
				//	&& (commit.getBranches().contains("product"))
					&& (m.getNewPath().startsWith(Main.pathToWhereCustomizationsAreComputed)))
			{
				String parentSha;
				String fileName;
		
				parentSha=commit.getParent();
				//
				fileName=m.getNewPath() + "/" + m.getFileName();
				System.out.println("changed file: "+fileName);
				
				DiffParser parsedDiff = new DiffParser(m.getDiff());
				int numberOfBlock= parsedDiff.getBlocks().size();//who many block in the diff
				
				
				int counter=1;
				List<DiffLine> addedNewLines,deletedInOld;
				String sourceCodeFile;
				
				System.out.println("\n\n\n------------------Commit:"+commit.getHash()+"---------");
				System.out.println("ProductBranch-- File name--- Feature changed ---Operation ---LOCs");
				System.out.println("------------------------------------------------------------------");
				
				
				while (counter<=numberOfBlock){//analyze those lines that appeared in the file
						addedNewLines = parsedDiff.getBlocks().get(counter-1).getLinesInNewFile();
						sourceCodeFile= m.getSourceCode();
						
						CustomizationEffort analyzeFeatureDetail = new CustomizationEffort(sourceCodeFile,addedNewLines);
						
						ArrayList<CustomizationEffort> list = analyzeFeatureDetail.computeFeatureChanged(sourceCodeFile, addedNewLines, m.getFileName(), m.getOldPath()) ;
						Iterator<CustomizationEffort> it= list.iterator();
						CustomizationEffort aux ;
						while (it.hasNext()){
							aux = it.next();
							System.out.println(commit.getBranches()+"  "+m.getFileName()+" "+aux.getFeatureModifiedName()+" " +aux.getOperation()+ " "+ aux.getNumLinesOfCode());
							if( (aux.getFeatureModifiedName()!="none") && (aux.getFeatureModifiedName()!=null)){
								writer.write(commit.getBranches(),aux.getFeatureModifiedName(),aux.getNumLinesOfCode());
								//ENTRY FOR FEATURE-FILE, value
								writer.write(aux.getFeatureModifiedName(),m.getFileName(),aux.getNumLinesOfCode());
							}
						}
						counter++;
					}	
					
				counter=1;//restart counter
					
					while (counter<=numberOfBlock){//analyze those lines that disapeared in the old file
						deletedInOld = parsedDiff.getBlocks().get(counter-1).getLinesInOldFile();
						sourceCodeFile= m.getSourceCode();
						AnalyzeFeatureDetail analyzeFeatureDetail = new AnalyzeFeatureDetail(sourceCodeFile,deletedInOld);
						ArrayList<CustomizationEffort> list = analyzeFeatureDetail.computeFeatureChanged(sourceCodeFile, deletedInOld, m.getFileName(), m.getOldPath());
						Iterator<CustomizationEffort> it= list.iterator();
						CustomizationEffort aux ;
						while (it.hasNext()){
							aux = it.next();
							System.out.println(commit.getBranches()+"  "+m.getFileName()+" "+aux.getFeatureModifiedName()+" " +aux.getOperation()+ " "+ aux.getNumLinesOfCode());
							if( (aux.getFeatureModifiedName()!="none") && (aux.getFeatureModifiedName()!=null)){
								//WRITE FOR PRODUCT-FEATURE
								writer.write(commit.getBranches(),aux.getFeatureModifiedName(),aux.getNumLinesOfCode());
								//ENTRY FOR FEATURE-FILE, value
								writer.write(aux.getFeatureModifiedName(), m.getFileName(), aux.getNumLinesOfCode());
							}
						}
						counter++;
					}
				}
		
	}

}
*/