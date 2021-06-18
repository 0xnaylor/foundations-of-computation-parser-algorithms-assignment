import computation.contextfreegrammar.*;
import computation.parser.IParser;
import computation.parsetree.ParseTreeNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CKYParser implements IParser {

    @Override
    public boolean isInLanguage(ContextFreeGrammar cfg, Word w) {

        // create table nxn, where n = |w|
        // and each cell contains an ArrayList of symbols
        int wordLen = w.length();

        //initialise table
        int x_axis_len = wordLen;
        int y_axis_len = wordLen;
        ArrayList<ArrayList<ArrayList<Symbol>>> table1 = new ArrayList<>(x_axis_len);

        for(int i=0; i<y_axis_len; i++) {
            table1.add(new ArrayList<>(y_axis_len));
            for(int j=0; j<x_axis_len; j++) {
                table1.get(i).add(new ArrayList<>());
            }
        }

        Symbol startVar = cfg.getStartVariable();
        List<Rule> rules = cfg.getRules();

        // handle scenario where input word is the empty string
        if(w.equals(Word.emptyWord)){
            for (Rule rule:rules) {
                if (rule.getVariable().equals(startVar) && rule.getExpansion().equals(Word.emptyWord)){
                    return true;
                }
            }
        }

        // For i = 1 to n
        // For each variable A:
        // Test whether A->b is a rule, where b = the i'th terminal in the word
        // If so, place A in table(i,i)
        for(int i=0;i<x_axis_len;i++){
            for (Rule rule:rules) {
                Symbol expansion = rule.getExpansion().get(0);
                Symbol terminal = w.get(i);
               if(expansion.equals(terminal)){
                   table1.get(i).get(i).add(rule.getVariable());
               }
            }

        }

        // generate next rows in table
        // l = length of substring
        for(int l=2; l<=wordLen;l++){
            for(int i=0;i<(wordLen-l+1);i++){ // start of Substring
                int subStringEnd = i+l-1;
                for(int k=i; k<subStringEnd; k++) {
                    for (Rule rule:rules) {
                        if(rule.getExpansion().length() == 2){
                            Symbol var1 = rule.getExpansion().get(0);
                            Symbol var2 = rule.getExpansion().get(1);
                            if(table1.get(i).get(k).contains(var1) && table1.get(k+1).get(subStringEnd).contains(var2)){
                                table1.get(i).get(subStringEnd).add(rule.getVariable());
                            }
                        }

                    }
                }
            }
        }


        System.out.println(w);
        System.out.println(returnPrintableTable(table1));

        // if S is in table(1, n), accept, else reject
        return table1.get(0).get(wordLen-1).contains(cfg.getStartVariable());

    }

    public String returnPrintableTable(ArrayList<ArrayList<ArrayList<Symbol>>> table){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < table.size(); i++){
            for(int j = 0; j < table.get(0).size(); j++) {
                int count;
                sb.append(table.get(i).get(j));
                count = table.get(i).get(j).size();
                while (count < 10) {
                    sb.append(' ');
                    count++;
                }
            }
            sb.append("\n");
        }
        return sb.toString();

    }


    @Override
    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
        return null;
    }
}
