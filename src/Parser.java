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

    for (Derivation derivation:allPossibleDerivations) {
      System.out.println(returnPrintableDerivation(derivation));
    }
    System.out.println("Found " + allPossibleDerivations.size() + " different derivations for word: " + w + "\n");

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

  private String returnPrintableDerivation(Derivation derivation) {

    StringBuilder sb = new StringBuilder();
    for (Step step : derivation) {
      sb.append(step);
      sb.append("\n");
    }
    return sb.toString();
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

    // get the symbols required to build a parse tree
//    Set<Variable> variables = cfg.getVariables();
//    Set<Terminal> terminals = cfg.getTerminals();

    Derivation derivation = allPossibleDerivations.get(0);

    System.out.println("The derivation: \n");
    System.out.println(returnPrintableDerivation(derivation));

    // generate ParseTreeNodes for each terminal in the word
//    System.out.println("terminals: ");
//    List<ParseTreeNode> terminalParseTreeNodes = new ArrayList<>();
//    for (Symbol symbol:w) {
//      terminalParseTreeNodes.add(new ParseTreeNode(symbol));
//      System.out.println(symbol);
//    }
//
//    List<ParseTreeNode> singleChildNodes = new ArrayList<>();
//    for (ParseTreeNode parseTreeNode:terminalParseTreeNodes) {
//      for (Step step : derivation) {
//
//        Rule stepRule = step.getRule();
//
//        if(stepRule.getExpansion().equals(parseTreeNode.getSymbol())) {
//          singleChildNodes.add(new ParseTreeNode(stepRule.getVariable(), parseTreeNode));
//        }
//      }
//    }

//    List<ParseTreeNode> nodes = new ArrayList<>();
    Map<Integer, ParseTreeNode> nodes = new HashMap<Integer, ParseTreeNode>();


    for (Step step:derivation) {

      Rule rule = step.getRule();

      if(step.getIndex() < 0) {
        continue;
      }

      // if the rule has a single terminal in its expansion
      // - create a childless node for that terminal
      // - create a single child node for the rules variable with the terminal as its child.
      // - Store variable node in ArrayList using step index
      if(rule.getExpansion().isTerminal()){
        Symbol terminal = rule.getExpansion().get(0);
        ParseTreeNode childNode = new ParseTreeNode(terminal);
        Symbol variable = rule.getVariable();
        ParseTreeNode parentNode = new ParseTreeNode(variable, childNode);
        nodes.put(step.getIndex(), parentNode);
        System.out.println(parentNode);
      }

      // if rule has the empty string (epsilon) in its expansion
      // - add a new emptyParseTree node to the ArrayList at the step index
      if(rule.getExpansion().equals(Word.emptyWord)){
        nodes.put(step.getIndex(), ParseTreeNode.emptyParseTree(rule.getVariable()));
      }

      // if the rule has two variables in its expansion
      // - create two single child nodes for those two variables
      // - create a two-child node for the rules variable
      if(rule.getExpansion().length() == 2) {
        Word expansion = rule.getExpansion();

        // create child nodes
        ParseTreeNode childNode1 = nodes.get(step.getIndex()+1);
        ParseTreeNode childNode2 = nodes.get(step.getIndex()+2);

        // create parent node and add to nodes ArrayList
        ParseTreeNode parentNode = new ParseTreeNode(rule.getVariable(), childNode1, childNode2);
        nodes.put(step.getIndex(), parentNode);
        System.out.println(parentNode);

      }
    }

    return nodes.get(0);
  }

}