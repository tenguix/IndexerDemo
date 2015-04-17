import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Iterator;
import java.util.Scanner;

public class Demo {


      /* Read entries from a file into the inverted index. */

    public static void readFile
            (String fname, Indexer<String,String> indexer)
    {
        Scanner sc;
        String basename;
        try {
            File f = new File(fname);
            basename = f.getName();
            sc = new Scanner(f);
        } //end try
        catch(FileNotFoundException ex) {
            System.err.printf("!ERROR: File \"%s\" not found.", fname);
            return;
        } //end catch
          /*
           *  We consider hyphenated words to be valid tokens,
           *  so we use an ugly delimiter which is essentially
           *  the inverse of "\\w+(?:-\\w+)*".
           */
        sc.useDelimiter("-*(?:[^\\w-]+-*)+");
        long start, end;
        int seen = 0, unique = 0;
        System.err.printf("Indexing \"%s\" ... ", basename);
        start = System.currentTimeMillis();
        while(sc.hasNext()) {
            if(indexer.add(sc.next().toLowerCase(), basename)) {
                ++unique;
            } //end while:if
            ++seen;
        } //end while
        end = System.currentTimeMillis();
        System.err.printf("added %3d of %3d entries processed in %2d ms%n",
                                unique, seen,                  end - start);
    } //end readFile

      /* Try to do what the user wants us to do. */

    public static void interpretCommand
            (String command, Scanner sc, Indexer<String,String> indexer)
            throws IOException, InterruptedIOException
    {
        if(command.equals("isempty"))
            System.out.println(indexer.isEmpty());
        else if(command.equals("getcount"))
            System.out.println(indexer.getCount());
        else if(command.equals("print")) {
            Iterator<String> kit = indexer.keyIterator();
            while(kit.hasNext()) {
                String key = kit.next();
                Iterator<String> vit = indexer.valueIterator(key);
                System.out.print(key +": {"+ vit.next());
                while(vit.hasNext())
                    System.out.print(", "+ vit.next());
                System.out.println("}");
            } //end elseif:while
        } //end elseif
        else if(command.equals("query"))
            if(sc.hasNext())
                System.out.println(interpretQuery(sc, false, indexer));
            else
                throw new InterruptedIOException();
        else if(command.equals("contains"))
            if(sc.hasNext())
                System.out.println(indexer.contains(sc.next()));
            else
                throw new InterruptedIOException();
        else if(command.equals("get"))
            if(sc.hasNext())
                System.out.println(indexer.get(sc.next()));
            else
                throw new InterruptedIOException();
        else if(command.equals("getall"))
             if(sc.hasNext())
                 printArray(indexer.getAll(sc.next()));
            else
                throw new InterruptedIOException();
        else if(command.equals("remove"))
            if(sc.hasNext())
                System.out.println(indexer.remove(sc.next()));
            else
                throw new InterruptedIOException();
        else if(command.equals("removeall"))
            if(sc.hasNext())
                printArray(indexer.removeAll(sc.next()));
            else
                throw new InterruptedIOException();
        else if(command.equals("add")) {
            if(sc.hasNext()) {
                String key = sc.next();
                if(sc.hasNext()) {
                    System.out.println(indexer.add(key, sc.next()));
                    return;
                } //end elseif:if:if
            } //end elseif:if
            throw new InterruptedIOException();
        } //end elseif
        else
            throw new IOException("Command '"+ command +"' not recognized.");
    } //end interpretCommand

    public static void printArray
            (Object[] array)
    {
        System.out.print("{");
        if(array.length > 0) {
            System.out.print(array[0]);
            for(int i = 1; i < array.length; ++i)
                System.out.print(", "+ array[i]);
        } //end if
        System.out.println("}");
    } //end printArray

      /* Interpret more complex queries. */

    public static Chain<String> interpretQuery
            (Scanner sc, boolean nested, Indexer<String,String> indexer)
            throws IOException, InterruptedIOException
    {
        Chain<String> left = null, right = null;
        String op = null;
        while(sc.hasNext()) {
            if(left != null && right != null) {
                if(op.equals("|")) {
                    Iterator<String> it = right.iterator();
                    while(it.hasNext())
                        left.add(it.next());
                } //end while:if:if
                else if(op.equals("&"))  {
                    Iterator<String> it = left.iterator();
                    while(it.hasNext()) {
                        String value = it.next();
                        if(!right.contains(value))
                            left.remove(value);
                    } //end while:if:elseif:while
                } //end while:if:elseif
                else if(op.equals("!")) {
                    Iterator<String> it = left.iterator();
                    while(it.hasNext()) {
                        String value = it.next();
                        if(right.contains(value))
                            left.remove(value);
                    } //end while:if:elseif:while
                } //end while:if:elseif
                else { //xor
                    Chain<String> old_left = left;
                    left = new Chain<String>();
                    Iterator<String> it = old_left.iterator();
                    while(it.hasNext()) {
                        String value = it.next();
                        if(!right.contains(value))
                            left.add(value);
                    } //end while:if:else:while
                    it = right.iterator();
                    while(it.hasNext()) {
                        String value = it.next();
                        if(!old_left.contains(value))
                            left.add(value);
                    } //end while:if:else:while
                } //end while:if:else
                op = null;
                right = null;
            } //end while:if
            String token = sc.next();
            if(token.equals("(")) {
                if(left == null)
                    left = interpretQuery(sc, true, indexer);
                else if(op != null)
                    right = interpretQuery(sc, true, indexer);
                else
                    throw new IOException("Misplaced '(' operator.");
            } //end while:if
            else if(token.equals("=")) {
                if(op == null && !nested)
                    return left;
                else
                    throw new IOException("Misplaced '=' operator.");
            } //end while:elseif
            else if(token.equals(")")) {
                if(op == null && nested) {
                    if(left == null)
                        left = new Chain<String>();
                    return left;
                } //end while:elseif:if
                else
                    throw new IOException("Misplaced ')' operator.");
            } //end while:elseif
            else if(token.equals("|") ||token.equals("&")
                    ||token.equals("!") ||token.equals("^")) {
                if(left != null && op == null)
                    op = token;
                else
                    throw new IOException("Misplaced '"+ token +"' operator.");
            } //end while:elseif
            else if(left == null)
                left = makeChain(token, indexer);
            else if(op == null)
                throw new IOException("Unknown operator: '"+ token + "'");
            else
                right = makeChain(token, indexer);
        } //end while
        throw new InterruptedIOException();
    } //end interpretQuery

    public static Chain<String> makeChain
            (String key, Indexer<String,String> indexer)
    {
        Chain<String> chain = new Chain<String>();
        if(indexer.contains(key)) {
            Iterator<String> it = indexer.valueIterator(key);
            while(it.hasNext())
                chain.add(it.next());
        } //end if
        return chain;
    } //end makeChain


      /** Run the demonstration. **/


    public static void main
            (String[] argv)
            throws IOException
    {
        System.err.println("--- begin Demo ---");

          /* Init the dictionary. */
        Indexer<String,String> indexer = new Indexer<String,String>();

          /* Index tokens from files given on command line. */
        System.err.println("--- read test data --- ");
        for(String infile : argv)
            readFile(infile, indexer);

          /* Evaluate user input. */
        System.err.println("--- interactive queries ---");
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()) {
            String command = sc.next().toLowerCase();
            if(command.equals("quit")) {
                sc.close();
                break;
            } //end while:if
            interpretCommand(command, sc, indexer);
        } //end while

        System.err.println("--- end Demo ---");
    } //end main


} //end Demo
