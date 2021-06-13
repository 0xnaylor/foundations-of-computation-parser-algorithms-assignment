import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser {

  Derivation correctDerivation = null;

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

    List<Derivation> currentDerivations = new ArrayList<>();

    Derivation derivation_0 = new Derivation(new Word(cfg.getStartVariable()));
    currentDerivations.add(derivation_0);

    // sanity check
    System.out.println("Latest Word: " + currentDerivations.get(0).getLatestWord());

    List<Rule> rules = cfg.getRules();
    List<Derivation> nextDerivations = new ArrayList<>();

    int stepsToPerform = (2 * w.length()) -1;
    for(int i = 1 ; i <= stepsToPerform ; i++) {
      nextDerivations = generateNextDerivations(currentDerivations, rules);
      System.out.println("Step " + i);
      System.out.println("Derivations produced: " + nextDerivations.size());
      System.out.println(returnPrintableDerivation(nextDerivations));
      currentDerivations = nextDerivations;
    }

    boolean isInLanguage = false;

    for (Derivation derivation:nextDerivations) {
      if(derivation.getLatestWord().equals(w)){
        isInLanguage = true;
        correctDerivation = derivation;
      }
    }

    System.out.println("Print correct Derivation");
    // find a way to print out the derivation correctly.
    while (correctDerivation.iterator().hasNext()) {
      System.out.println(correctDerivation.iterator().next().toString());
    }


    return isInLanguage;

    // 5. Repeat step 3 until we have 2n-1 derivations.
    // 6. Loop through the final list and check if the latest word for each Derivation matches the target word.


//    System.out.println("Derivation Length: " + stepsToPerform);

    // Add the start variable to the list
//    int stepsToPerform = (2 * w.length()) -1;
//    currentStep.add(new Word(cfg.getStartVariable()));
//    System.out.println("Step 0 \n" + currentStep);
//
//    for(int i = 1 ; i <= stepsToPerform ; i++) {
//      List<Word> nextStep = generateNextStep(currentStep, rules);
//      System.out.println("Step " + i);
//      System.out.println("Strings produced: " + nextStep.size());
//      System.out.println(nextStep);
//      currentStep = nextStep;
//    }
  }

  private String returnPrintableDerivation(List<Derivation> nextDerivations) {
    StringBuilder sb = new StringBuilder();
    for (Derivation derivation: nextDerivations) {
      sb.append(derivation.getLatestWord().toString());
      sb.append(" ,");
    }
    return sb.toString();
  }

  private List<Derivation> generateNextDerivations(List<Derivation> currentDerivations, List<Rule> rules) {
    List<Derivation> nextDerivations = new ArrayList<>();

    for (Derivation derivation: currentDerivations) {
      Word word = derivation.getLatestWord();

        for (int i = 0; i < word.length(); i++) {

          Symbol s = word.get(i);
          if (!s.isTerminal()) {

            for (Rule rule : rules) {
              if (rule.getVariable().equals(s)) {
                Derivation newDerivation = new Derivation(derivation);
                Word newWord = word.replace(i, rule.getExpansion());
                newDerivation.addStep(newWord, rule, 1);
                nextDerivations.add(newDerivation);
              }
            }
          }
        }
    }
    return nextDerivations;
  }

  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {

    ParseTreeNode testNode = new ParseTreeNode(cfg.getStartVariable());

    return testNode;
  }

  private static List<Word> generateNextStep(List<Word> currentStep, List<Rule> rules) {

    // the new list to be created by applying one more derivation step to all words in current step.
    List<Word> nextStep = new ArrayList<>();

    for (Word word : currentStep) {
      int wordLength = word.length();
      for (int i = 0; i < wordLength; i++) {
        Symbol s = word.get(i);

        if (!s.isTerminal()) {

          for (Rule rule : rules
          ) {
            if (rule.getVariable().equals(s)) {
              // take the current word and replace the variable in question with the rule expansion and add to new string.
              nextStep.add(word.replace(i, rule.getExpansion()));
            }
          }
        }
      }
    }
    return nextStep;
  }

}