import computation.contextfreegrammar.ContextFreeGrammar;
import computation.contextfreegrammar.Rule;
import computation.contextfreegrammar.Symbol;
import computation.contextfreegrammar.Word;
import computation.parser.IParser;
import computation.parsetree.ParseTreeNode;

import java.util.List;

public class CKYParser implements IParser {

    @Override
    public boolean isInLanguage(ContextFreeGrammar cfg, Word w) {

        // create table nxn, where n = |w|
        int wordLen = w.length();
        Symbol[][] table = new Symbol[wordLen][wordLen];

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

        // generate the first row of the table, i.e. examining each substring of length 1
        for(int i=0;i<wordLen;i++){
            for (Rule rule:rules) {
               if(rule.getExpansion().toString().equals(w.get(i).toString())){
                   table[0][i] = rule.getVariable();
               }
            }
        }

        System.out.println(returnPrintableTable(table));





        return false;
    }

    public String returnPrintableTable(Symbol[][] table){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < table.length; i++){
            for(int j = 0; j < table[0].length; j++){

                // if value at array index is not the default char '\u0000 print the value held at the index.
                if (table[i][j] != null) {
                    sb.append("| ").append(table[i][j]).append(" ");
                } else {
                    sb.append("|   ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();

    }

    @Override
    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
        return null;
    }
}
