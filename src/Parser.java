import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

import java.util.*;

public class Parser implements IParser {

  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){

    // 1. Create a new list of type Derivation
    // 2. Instantiate one new Derivation object containing only the start variable and add it to the list.
    // 3. Apply every applicable substitution rule to the start variable.
    //    For each outcome:
    //      - Instantiate a new Derivation from the existing one
    //      - Add a new step containing the rule used
    //      - Store the new Derivation Object in a new list
    // 5. Repeat step 3 until we have 2n-1 derivations.
    // 6. Loop through the final list and check if the latest word for each Derivation matches the target word.
    boolean isInLanguage = false;

    List<Derivation> allPossibleDerivations = getAllPossibleDerivations(cfg, w);

    if (!allPossibleDerivations.isEmpty()) {
      isInLanguage = true;
    }

    return isInLanguage;
  }

  public List<Derivation> getAllPossibleDerivations(ContextFreeGrammar cfg, Word w){
    List<Derivation> currentDerivations = new ArrayList<>();
    Derivation derivation_0 = new Derivation(new Word(cfg.getStartVariable()));
    currentDerivations.add(derivation_0);

    List<Rule> rules = cfg.getRules();
    List<Derivation> nextDerivations = new ArrayList<>();
    int derivationStepIndex = 0;
    int stepsToPerform = (2 * w.length()) -1;
    for(int i = 1 ; i <= stepsToPerform ; i++) {
      nextDerivations = generateNextDerivations(currentDerivations, rules, derivationStepIndex);
      currentDerivations = nextDerivations;
      derivationStepIndex+=1;
    }

    List<Derivation> allPossibleDerivations = new ArrayList<>();
    for (Derivation derivation:nextDerivations) {
      if(derivation.getLatestWord().equals(w)){
        allPossibleDerivations.add(derivation);
      }
    }

    return allPossibleDerivations;
  }

  private List<Derivation> generateNextDerivations(List<Derivation> currentDerivations, List<Rule> rules, int derivationStepIndex) {
    List<Derivation> nextDerivations = new ArrayList<>();

    // for each Derivation in currentDerivation list
    for (Derivation derivation: currentDerivations) {
      Word word = derivation.getLatestWord();

      // for each Variable in the word
      for (int i = 0; i < word.length(); i++) {
        Symbol s = word.get(i);
        if (!s.isTerminal()) {
          for (Rule rule : rules) {
            // if the Variable on the left of the rule matches the currently selected Variable in the word
            if (rule.getVariable().equals(s)) {
              Derivation newDerivation = new Derivation(derivation);      // instantiate a new Derivation from the existing one
              Word newWord = word.replace(i, rule.getExpansion());        // perform the substitution
              newDerivation.addStep(newWord, rule, derivationStepIndex);  // add a step to the new Derivation.
              nextDerivations.add(newDerivation);                         // add the new Derivation to the nextDerivations list.
            }
          }
        }
      }
    }
    return nextDerivations;
  }

  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {

    // get the derivations for the word
    List<Derivation> allPossibleDerivations = getAllPossibleDerivations(cfg, w);

    if(allPossibleDerivations.size() > 0) {
      Derivation derivation = allPossibleDerivations.get(0);

      Map<Symbol, ParseTreeNode> nodes = new HashMap<>();

      for (Step step:derivation) {

        // if the start variable has been reached, i.e. step index -1 then we
        // have nothing left to add to the tree
        if(step.getIndex() < 0) { continue;}

        Rule rule = step.getRule();

        // create single-child and childless nodes
        if(rule.getExpansion().isTerminal()){

          Symbol terminal = rule.getExpansion().get(0);
          ParseTreeNode childNode = new ParseTreeNode(terminal);

          Symbol variable = rule.getVariable();
          ParseTreeNode parentNode = new ParseTreeNode(variable, childNode);
          nodes.put(variable, parentNode);
        }

        // create emptyParseTree node
        if(rule.getExpansion().equals(Word.emptyWord)){
          Variable variable = rule.getVariable();
          nodes.put(variable, ParseTreeNode.emptyParseTree(variable));
        }

        // create two-children nodes
        if(rule.getExpansion().length() == 2) {

          Word expansion = rule.getExpansion();
          ParseTreeNode childNode1 = nodes.get(expansion.get(0));
          ParseTreeNode childNode2 = nodes.get(expansion.get(1));

          Variable variable = rule.getVariable();
          ParseTreeNode parentNode = new ParseTreeNode(rule.getVariable(), childNode1, childNode2);
          nodes.put(variable, parentNode);
        }
      }

      return nodes.get(cfg.getStartVariable());
    } else {
      return null;
    }

  }

}