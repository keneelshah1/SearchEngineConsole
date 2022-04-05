package search;

import org.jsoup.Jsoup;
import textprocessing.In;

import java.io.PrintWriter;
import java.util.ArrayList;


public class HtmlText {

    public static void mainht() {

        ArrayList<String> urlList = new ArrayList<>();

        //Uses URL of our own PC while running on local computer
        // URLs in this file will get stored when we execute the webCrawler file
        // Crawling "the entered url" and then storing all the URLs in file 
        In in = new In("D:\\UWindsor\\ACC\\Search Engine Console\\crawled.txt");

        while (!in.isEmpty()) {

            String myText = in.readLine();
            urlList.add(myText);

        }


        for (int i = 0; i < urlList.size(); i++) {

            try {
            	// this will store the content in url stored at index i in the given folder.
            	//entire text content is stored in text file with its index as name
                org.jsoup.nodes.Document doc = Jsoup.connect(urlList.get(i)).get(); //will connect the url stored in 'i' index
                String text = doc.text();
                //creates file in URLs folder with array index as its file name
                String FilePath = "D:\\UWindsor\\ACC\\Search Engine Console\\urlsFiles\\" + (i) + ".txt";
                PrintWriter pw = new PrintWriter(FilePath);
                
              
                pw.println(urlList.get(i));
                pw.println(text);
                System.out.println(urlList.get(i));
                pw.close();


            } catch (Exception e) {

                System.out.println("Exception, URL cannot be converted to text: " + urlList.get(i));
            }


        }
    }
}
