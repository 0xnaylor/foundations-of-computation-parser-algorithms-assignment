import computation.contextfreegrammar.*;
import computation.parser.IParser;
import computation.parsetree.ParseTreeNode;
import java.util.ArrayList;
import java.util.List;

public class CKYParser implements IParser {

    ArrayList<ArrayList<ArrayList<Symbol>>> derivationTable = new ArrayList<>();

    @Override
    public boolean isInLanguage(ContextFreeGrammar cfg, Word w) {

        // create table nxn, where n = |w|
        // and each cell contains an ArrayList of symbols
        int wordLen = w.length();

        for(int i=0; i<wordLen; i++) {
            derivationTable.add(new ArrayList<>(wordLen));
            for(int j=0; j<wordLen; j++) {
                derivationTable.get(i).add(new ArrayList<>());
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
        for(int i=0;i<wordLen;i++){
            for (Rule rule:rules) {
                Symbol expansion = rule.getExpansion().get(0);
                Symbol terminal = w.get(i);
               if(expansion.equals(terminal)){
                   derivationTable.get(i).get(i).add(rule.getVariable());
               }
            }

        }

        // generate next rows in table
        // l = length of substring
        int n = wordLen;
        for(int l=2; l<=n;l++){ // loop through sub strings of length 2 up to the full word
            for(int i=1;i<=(n-l+1);i++){ // substring start
                int j = i+l-1; // generate a substring end based on substring start and substring length
                for(int k=i; k<=j-1; k++) { // substring end
                    for (Rule rule:rules) {
                        if(rule.getExpansion().length() == 2){
                            Symbol var1 = rule.getExpansion().get(0);
                            Symbol var2 = rule.getExpansion().get(1);
                            if(derivationTable.get(i-1).get(k-1).contains(var1) && derivationTable.get(k).get(j-1).contains(var2)){
                                derivationTable.get(i-1).get(j-1).add(rule.getVariable());
                            }
                        }

                    }
                }
            }
        }

        // if S is in table(1, n), accept, else reject
        return derivationTable.get(0).get(wordLen-1).contains(cfg.getStartVariable());

    }

    @Override
    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
        return null;
    }
}
