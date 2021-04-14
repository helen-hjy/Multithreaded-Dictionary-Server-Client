/*
 * COMP90015 - Assignment 1
 * Full name: Jiayu Han
 * ID: 1164280
 * 
 */

package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

public class DictionaryServer {
	
	private static int counter = 0; //count the client
	private static String filepath; //get filepath
	private static int port; //get port number

 	public static void main(String[] args)
	{	
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		// get arguments
		port = Integer.parseInt(args[0]); //port number
		filepath = args[1]; //dictionary path
		
		try(ServerSocket server = factory.createServerSocket(port))
		{
			// Wait for connections for each client
			while(true)
			{
				Socket client = server.accept();//accept the client
				counter++;			 			// get a new client
				System.out.println("Thread "+counter+": connection successful!");
				
				// Start a new thread for each request
				Thread t = new Thread(() -> serveClient(client));
				t.start();
			}
			
		} 
		catch (IOException e)
		{
			System.out.println("Socket failed to create!");
		}
		
	}
	//run the client
	private static void serveClient(Socket client)
	{
		try(Socket clientSocket = client)
		{
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
		    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
		    
		    //receive command from the server
		    String command = input.readUTF(); //get text from input stream
		    if(command.contains(":")) {
		    	String receiver[] = command.split(":"); //split operation and text
			   
			    //process four operations and write into output stream
			    if (receiver[0].equals("query")){   
			    	//output in terminal
					System.out.println(" Thread "+counter+": Process query...");
			    	output.writeUTF(query(receiver[1]));
			    	
			    }
			    if(receiver[0].equals("add")) {
					//output in terminal
					System.out.println(" Thread "+counter+": Process adding... ");
			    	output.writeUTF(add(receiver[1]));
			    	
			    }
			    if(receiver[0] .equals( "remove")) {
			    	//output in terminal
					System.out.println(" Thread "+counter+": Process removal...");
			    	output.writeUTF(remove(receiver[1]));
			    	
			    }
			    if(receiver[0] .equals( "update"))
			    {
			    	//output in terminal
					System.out.println(" Thread "+counter+": Process update...");
			    	output.writeUTF(update(receiver[1]));
			    }
		    }
		    else {
		    	System.out.println("Error: not valid command - no :");
		    	
		    }    
		} 
		catch (IOException e) 
		{
			System.out.println("Socket failed to run! (line 51)");
		}
	}
	
	//process query 
	public static String query(String input) {
		input = input.trim();//remove all space
		
		String message = "";
		//default message
		message = "Error: No such word: \"" + input + "\" in dictionary file.";
		
		//error: not valid input
		if(input.contains(",")) {
			message = "Error: Not valid input - should not include comma for query.";
		}
		else {
			try {
				String line ="";	
				BufferedReader reader = new BufferedReader(new FileReader(filepath));		
				while((line = reader.readLine()) != null)
				{
					  String[] fields = line.split(",");
					  //find the query word
					  if(fields[0].trim().equals(input)) 
					  {
						  //return the explanation
						  message = fields[1].trim();					  
						  message = "Explanation found: " + message;
					  }
				}
			} catch (FileNotFoundException e) {
				System.out.println("Have not found file in: "+filepath);		
			} catch (IOException e) {
				System.out.println("Cannot read the file "+filepath+" from Bufferreader (line 117)");
			}
		}
		
		System.out.println(" Operation result: "+message);
		return message;	
		
	}
	
	//add a new word
	public static String add(String input) {
		String message = "";
		input = input.trim();		// remove all space
			
		try {
			FileWriter writer = new FileWriter(filepath, true); //append file by filewriter
			
			//split the word
			if(input.contains(","))
			{	
				String text[] = input.split(",");			
				//error: no explanation
				if(text.length==1) {
					message = "Error: empty explanation.";
				}
				//while valid input
				else {				
					writer.append(text[0].trim()+","+text[1].trim()+"\n");				
					writer.close();
					//update message
					message = "New word: " + text[0] + " with its explanation is added!";
				}
				
			}
			//if word has no comma
			else {
				
				message = "Error: Not valid input - no comma separating word and explanation.";
			}
			
		} catch (IOException e) {
			System.out.println("Cannot write file in: "+filepath);
		}
		
		System.out.println(" Operation result: "+message);
		return message; 
	}
	
	//remove a word
	public static String remove(String input) {
		input = input.trim();
		
		String message = "";	
		//default message
		message = "Error: No such word: \"" + input + "\" in dictionary file.";
		
		//error: not valid input
		if(input.contains(",")) {
			message = "Error: Not valid input - should not include comma for removal";
		}
		//with valid input
		else {
			try {
				String line ="";
				String file = "";
				@SuppressWarnings("resource")
				BufferedReader reader = new BufferedReader(new FileReader(filepath));
							
				while((line = reader.readLine()) != null)
				{			
					  String[] fields = line.split(",");
					  fields[0] = fields[0].trim();
					  fields[1] = fields[1].trim();					  
					  //search the word
					  if(fields[0].trim().equals(input)) {						  
						  file += "";		//replace word with null		  
						  message = "Word: "+input+" removed successfully!";		  		  
					  }
					  else {
						  //if not the word to remove, write into file
						  file = file + line + "\n";
					  }					  
				}
				
				//overwrites the file
				FileWriter fw = new FileWriter(filepath,false); 
				fw.write(file);		
				fw.close();
				
			} catch (FileNotFoundException e) {
				System.out.println("Have not found file in: "+filepath);		
			} catch (IOException e) {
				System.out.println("Cannot read the file "+filepath+" from Bufferreader (line 117)");
			}
		}
		
		System.out.println(" Operation result: "+message);
		return message;	
	}
	
	//update explanation of a word
	public static String update(String input) {
		input = input.trim(); //remove space
	
		String message = "";	

		//input contains comma
		if(input.contains(",")) {
			String[] text = input.split(","); //split input into text[]
			
			//default message
			message = "Error: No such word: \"" + text[0] + "\" in dictionary file.";		
			
			//error: no explanation
			if(text.length==1) {
				message = "Error: empty explanation.";
			}
			
			//remove spaces
			text[0] = text[0].trim();
			text[1] = text[1].trim();
					
			//when valid input
			try {
				String line ="";
				String file = "";
				BufferedReader reader = new BufferedReader(new FileReader(filepath));
				//read the file line by line
				while((line = reader.readLine()) != null)
				{									
					String[] fields = line.split(","); //split the word and explanation
					//remove all space
					fields[0] = fields[0].trim();
					fields[1] = fields[1].trim();
				    //compare the word with input
				    if(fields[0].equals(text[0])) 
				    {						  
					    file = file + fields[0] + "," + text[1] + "\n";	 //update explanation	
					    //update message
					    message = "Update \"" + fields[0] + "\" explanation successfully!";
				    }
				    //if the line is not the word, append into string-"file"
				    else {			   
					    file = file + line + "\n";
				    }					  
				}
				
				//overwrites the file
				FileWriter fw = new FileWriter(filepath,false); 
				fw.write(file);		
				fw.close();
				
			} catch (FileNotFoundException e) {
				System.out.println("Have not found file in: "+filepath);		
			} catch (IOException e) {
				System.out.println("Cannot read the file "+filepath+" from Bufferreader (line 117)");
			}
		}
		//when no comma
		else {
			message = "Error: Not valid input - should use comma to separate word and explanation.";
		}
		
		System.out.println(" Operation result: "+message);
		return message;	
	}

}

