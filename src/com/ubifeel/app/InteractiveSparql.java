/**
 * A simple tool to interact with Sparql based on Jena and Pellet. 
 */
package com.ubifeel.app;

import java.util.Scanner;

/**
 * @author Ze Ji
 *
 */
public class InteractiveSparql {

	public InteractiveSparql() 
	{
		System.out.println("");
		System.out.println("*********************************\n");
		System.out.println("Simple Interactive Sparql Console\n");
		System.out.println("*********************************\n");
		System.out.println("*       quit or q to quit.      *\n");
		System.out.println("*********************************\n");
		System.out.println("");
		this.init();
	}

	private void init() {
		this.ontoDB = new OntologyDB();
	}
	
	public void startEngine() 
	{
		Scanner scanner = new Scanner(System.in);
		String input = "";
		while(!input.equals("quit") && !input.equals("q")) {
		    System.out.print("=>  ");
		    input = scanner.nextLine().trim();
		    if(input.length() != 0)
		    {
		    	this.processLine(input);
		    	//System.out.println("input = " + input);
		    }
		}
		scanner.close();
	}
	
	private boolean loadModel(String filename) 
	{
		try{
		this.ontoDB.importOntology(filename);
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	private void processLine(String line) 
	{
		if (line.length() == 0) 
		{
			throw new IllegalArgumentException("Empty command");
		}
		// TODO should use regular expression or other cool stuff
		if(line.startsWith("load(\"") && line.endsWith("\")"))
		{
			String filename = line.substring(6, line.length() - 2);
			System.out.println(filename);
			if(this.loadModel(filename)) 
			{
				System.out.println(filename + " is loaded");	
			}
			
		}
		else
		{
			String res = this.ontoDB.executeQuery(line);			
			System.out.println(res);
		}
		/*
		Pattern p = Pattern.compile("\bload(\B");
		Matcher m = p.matcher(line);
		 boolean b = m.matches();
		 if(b) 
		 {
		 System.out.println("Matches");
		 }
		 else
		 {
			 System.out.println("Not match");
		 }
		*/
		/*
		if(line.matches("load([a-zA-Z])"))
		{
			System.out.println("OK. Match ...");
		}
		*/
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InteractiveSparql insparql = new InteractiveSparql();
		try {
			insparql.startEngine();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private OntologyDB ontoDB;
}
