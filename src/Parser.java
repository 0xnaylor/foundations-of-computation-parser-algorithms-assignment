import computation.contextfreegrammar.*;
import computation.parser.*;
import computation.parsetree.*;
import computation.derivation.*;

import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser {



  public boolean isInLanguage(ContextFreeGrammar cfg, Word w){

    List<Rule> rules = cfg.getRules();
    int stepsToPerform = (2 * w.length()) -1;
    System.out.println("Derivation Length: " + stepsToPerform);

    // Add the start variable to the list
    List<Word> currentStep = new ArrayList<>();
    currentStep.add(new Word(cfg.getStartVariable()));

    for(int i = 1 ; i <= stepsToPerform ; i++) {
      List<Word> nextStep = generateNextStep(currentStep, rules);
      currentStep = nextStep;
    }

    return currentStep.contains(w);
  }

  public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
    return null;
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