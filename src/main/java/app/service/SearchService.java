package app.service;

import app.pojo.ApiNormalizedRule;
import contract.rules.AbstractRule;
import contract.searchRequests.RuleSearchRequest;
import contract.searchResults.SearchResult;
import org.springframework.stereotype.Service;
import repository.SearchRepository;

import java.util.List;

import static contract.rules.enums.RuleRequestCategory.DIGITAL;
import static ingestion.rule.JsonRuleIngestionService.getDigitalEventRules;
import static ingestion.rule.JsonRuleIngestionService.getRules;
import static java.util.stream.Collectors.toList;

@Service
public class SearchService {

    private SearchRepository<AbstractRule> ruleRepository;
    private SearchRepository<AbstractRule> digitalRuleRepository;

    public SearchService() {
        List<AbstractRule> rules = getRules();
        ruleRepository = new SearchRepository<>(rules);

        List<AbstractRule> digitalRules = getDigitalEventRules();
        digitalRuleRepository = new SearchRepository<>(digitalRules);
    }

    public List<ApiNormalizedRule> getRuleSearchResults(RuleSearchRequest ruleSearchRequest) {
        List<AbstractRule> output;
        if (ruleSearchRequest.getRuleRequestCategory() == DIGITAL) {
            output = digitalRuleRepository.getSearchResult(ruleSearchRequest)
                    .stream()
                    .map(SearchResult::getEntry)
                    .collect(toList());
        } else {
            output = ruleRepository.getSearchResult(ruleSearchRequest)
                    .stream()
                    .map(SearchResult::getEntry)
                    .collect(toList());
        }
        return normalizeRules(output);
    }

    private List<ApiNormalizedRule> normalizeRules(List<AbstractRule> rules) {
        return rules.stream()
                .map(this::normalizeRule)
                .collect(toList());
    }

    private ApiNormalizedRule normalizeRule(AbstractRule rule) {
        return new ApiNormalizedRule(
                normalizeRules(rule.getSubRules()),
                rule.getText(),
                getParentText(rule),
                rule.getRuleSource()
        );
    }

    private String getParentText(AbstractRule rule) {
        return rule.getParentRule() == null ?
                "" :
                getParentText(rule.getParentRule()) + " " + rule.getParentRule().getText();
    }
}
