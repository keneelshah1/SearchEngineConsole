package search;

import sorting.Sort;
import textprocessing.In;
import textprocessing.TST;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;


public class SearchMain 
{
	public static String src = "D:\\UWindsor\\ACC\\Search Engine Console\\";
    // removing the stop words
    // stop words are the words that caarry very little or no useful information
    public static String[] getKeywords(String inputString) 
    {
        int i = 0;
        In input = new In(src+"stop-words.txt");
        inputString = inputString.toLowerCase();

        while (!input.isEmpty()) 
        {
            String text = input.readLine();
            text = text.toLowerCase();
            text = "\\b" + text + "\\b";
            inputString = inputString.replaceAll(text, "");
        }

        StringTokenizer st = new StringTokenizer(inputString, " ");
        String[] keyWords = new String[st.countTokens()];

        while (st.hasMoreTokens()) 
        {
            keyWords[i] = st.nextToken();
            i++;
        }
        return keyWords;
    }


    //This methods is used to index URLs by fetching URLs from file and inserting each URL into Hashmap     
    public static HashMap<Integer, String> indexURLS() 
    {
        int i = 0;
        HashMap<Integer, String> UrlIndex = new HashMap<Integer, String>();
        In input = new In(src+"crawled.txt");

        while (!input.isEmpty()) 
        {
            String text = input.readLine();
            UrlIndex.put(i, text);
            i++;
        }
        return UrlIndex;
    }

    //This method is used to create TST of each text file
    public static TST <Integer> getTST(String fPath) 
    {
        int temp = 0;
        TST<Integer> tst = new TST<Integer>();
        In input = new In(fPath);

        while (!input.isEmpty()) 
        {
            String inputText = input.readLine();
            if (temp == 0) 
            {
                temp = 1;
                continue;

            } 
            else if (temp == 1) 
            {
                temp = 0;

                StringTokenizer stTok = new StringTokenizer(inputText, " ");
                while (stTok.hasMoreTokens()) 
                {
                    String word = stTok.nextToken();
                    word = word.toLowerCase();
                    
                    if (tst.contains(word)) 
                    {
                        tst.put(word, tst.get(word) + 1);   
                    } 
                    else 
                    {
                        tst.put(word, 1);
                    }
                }
            }
        }
        return tst;
    }

    //This method is used to find the count of total occurrence of the keywords input each text file
    public static HashMap<Integer, Integer> getFreqList(String[] keyWords) 
    {
        //Map each text file to its corresponding number into an arraylist
        ArrayList<String> textList = new ArrayList<>();
        HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();

        File folder = new File("D:\\UWindsor\\ACC\\Search Engine Console\\urlsFiles");
        File[] files = folder.listFiles();

        for (File file : files) 
        {
            String myURL = file.getName();
            //myURL = myURL.substring(0, (myURL.length()-4));
            textList.add(myURL);
        }

        for (int i = 0; i < textList.size(); i++) 
        {
            String filePath = "D:\\UWindsor\\ACC\\Search Engine Console\\urlsFiles\\";
            String fileName = textList.get(i);
            String fPath = filePath + fileName;
            
            String tempFileIndex = fileName.substring(0, (fileName.length() - 4));
            int fileIndex = Integer.parseInt(tempFileIndex);
            
            TST<Integer> tst = new TST<Integer>();
            tst = SearchMain.getTST(fPath);   //returns TST of given file

            int counter = 0;

            for (String str : keyWords) 
            {
                if (tst.contains(str)) 
                {
                    int count = tst.get(str); //returns subtree for given keyword
                    counter = counter + count;
                }
            }
            freqList.put(fileIndex, counter);
        }
        return freqList;
    }


    //based on values, this method is used to sort hashmap input descending order 
    public static HashMap<Integer, Integer> sortHashMap(HashMap<Integer, Integer> freqList) {
        HashMap<Integer, Integer> sortedFreqList = freqList
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        return sortedFreqList;
    }


    //This method is used to store the frequency list hashmap, which will be used input Page Ranking
    public static void storeHashMap(HashMap<Integer, Integer> freqList, String[] keyWords) {

        Sort.mergeSort(keyWords);
        String fileName = "";

        for (String str : keyWords) {

            fileName = fileName + str + "_";
        }

        fileName = fileName + ".dat";

        String filePath = src+"hashmap_data/";
        String fPath = filePath + fileName;

        try {

            FileOutputStream fileOut = new FileOutputStream(fPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(freqList);
            out.close();
            fileOut.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    //This method is used to retrieve the frequency list hashmap used for Page Ranking
    public static HashMap<Integer, Integer> retreiveHashMap(String[] keyWords) {

        Sort.mergeSort(keyWords);

        String fileName = "";

        for (String str : keyWords) {

            fileName = fileName + str + "_";
        }

        fileName = fileName + ".dat";
        String filePath = src+"hashmap_data/";
        String fPath = filePath + fileName;

        HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();
        freqList = null;

        try {

            FileInputStream fileIn = new FileInputStream(fPath);
            ObjectInputStream input = new ObjectInputStream(fileIn);
            freqList = (HashMap<Integer, Integer>) input.readObject();
            input.close();
            fileIn.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return freqList;

    }


    // main driver function which will give the final output
     
    public static void main(String[] args) {

    	Crawler.mainc();
    	HtmlText.mainht();
    	String searchQuery = "";
        
        System.out.println("Enter your search here: ");
        System.out.println();
        Scanner sc = new Scanner(System.in);
        searchQuery = sc.nextLine();
//        if(scanner.hasNextLine())
//        {	
//        	searchQuery = scanner.nextLine();
//        }
        
        long startTime = System.currentTimeMillis();
        
        String[] keyWords = SearchMain.getKeywords(searchQuery); //removes stop words from input keywords
        Sort.mergeSort(keyWords); // keywords sorted using merge sort 

        String fName = "";
        for (String str : keyWords) {

            fName = fName + str + "_";
        }

        fName = fName + ".dat"; //creates a file with name same as keyword

        boolean fileExist = false;
        
        // to store the files input hashmap_data folder

        File folder = new File(src+"hashmap_data"); 
        File[] files = folder.listFiles();

        for (File file : files) {

            String fileName = file.getName();

            if (fileName.compareTo(fName) == 0) {

                fileExist = true;
                break;

            }

        }

        if (fileExist == true) {

            System.out.println("File exists input our folder. Same keywords were searched previously\n");

            HashMap<Integer, String> urlIndex = new HashMap<Integer, String>();
            urlIndex = SearchMain.indexURLS();

            HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();
            freqList = SearchMain.retreiveHashMap(keyWords);

            System.out.println("Top 15 Search Results for \"" + searchQuery + "\" are:\n");

            int j = 0;
            for (HashMap.Entry<Integer, Integer> entry : freqList.entrySet()) {

                if (j < 15) {

                    //System.out.println(entry.getKey() + " = " + entry.getValue());
                    int urlKey = entry.getKey();
                    System.out.println(urlIndex.get(urlKey) + "\n");
                    j++;

                } else {

                    break;
                }
            }

        } else if (fileExist == false) 
        {

            System.out.println("File does not exist. This keyword was not searched previously. Creating a new file and storing data\n\n");
            HashMap<Integer, String> urlIndex = new HashMap<Integer, String>();
            urlIndex = SearchMain.indexURLS();

            HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();
            freqList = SearchMain.getFreqList(keyWords);

            freqList = SearchMain.sortHashMap(freqList);

            SearchMain.storeHashMap(freqList, keyWords);

            System.out.println("Top 15 Search Results for \"" + searchQuery + "\" are:\n");
            int j = 0;

            for (HashMap.Entry<Integer, Integer> entry : freqList.entrySet()) {

                if (j < 15) {

                    //System.out.println(entry.getKey() + " = " + entry.getValue());
                    int urlKey = entry.getKey();
                    System.out.println(urlIndex.get(urlKey) + "\n");
                    j++;

                } else {

                    break;
                }
            }

        }
        Long endTime = System.currentTimeMillis();
        
        Long totalTime = endTime - startTime;
        
        System.out.println("\n\nTime taken by search engine to return results:" + totalTime + " ms");
        
    }

}