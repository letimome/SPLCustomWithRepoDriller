package customDiff.SPLdomain;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jgit.revwalk.RevCommit;

import customDiff.utils.Utils;


public class ProductRelease {

	private String idRelease;
	private Product fromProduct;
	private Date releaseDate;
	private	ArrayList<SourceCodeFile> productAssets = new ArrayList<SourceCodeFile>();
	private RevCommit releasedCommit;
	private Iterable<RevCommit> listOfcommits;//list of commits from baseline to the release; In reverse order!
	ArrayList<CustomizationFact> customsList=new ArrayList<CustomizationFact>();
	
	
	
	public ProductRelease(String idRelease, Product fromProduct, Date l, RevCommit releasedCommit){
		
		this.fromProduct = fromProduct;
		this.releaseDate = l;
		this.releasedCommit = releasedCommit;	
		this.idRelease = Utils.extractRefName(idRelease, 2);
	}
	
	
	public void setCustomizations(ArrayList<CustomizationFact> customs){
		this.customsList = customs;
	}
	
	public ArrayList<CustomizationFact>  getCustomizations(){
		return this.customsList;
	}
	
	public String getIdRelease() {
		return idRelease;
	}
	public void setIdRelease(String tag) {
		this.idRelease = tag;
	}
	public Product getFromProduct() {
		return fromProduct;
	}
	public void setFromProduct(Product fromProduct) {
		this.fromProduct = fromProduct;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public ArrayList<SourceCodeFile> getProductAssets() {
		return productAssets;
	}
	public void setProductAssets(ArrayList<SourceCodeFile> productAssets) {
		this.productAssets = productAssets;
	}

	public RevCommit getReleasedCommit() {
		return releasedCommit;
	}

	public void setReleasedCommit(RevCommit releasedCommit) {
		this.releasedCommit = releasedCommit;
	}

	
	
	public Iterable<RevCommit> getListOfcommits() {
		return listOfcommits;
	}


	public void setListOfcommits(Iterable<RevCommit> listOfcommits) {
		this.listOfcommits = listOfcommits;
	}


	public ArrayList<CustomizationFact> getCustomsList() {
		return customsList;
	}


	public void setCustomsList(ArrayList<CustomizationFact> customsList) {
		this.customsList = customsList;
	}


	public String toString(){
		return "ProductRelease: "+idRelease+" belongs to: "+fromProduct+ " with last commit: "+releasedCommit.getName();
	}
	
	
	
	
} 
