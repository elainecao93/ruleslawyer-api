package app.controller;

import app.pojo.ApiNormalizedRule;
import app.service.SearchService;
import contract.rules.AbstractRule;
import contract.rules.enums.RuleRequestCategory;
import contract.rules.enums.RuleSource;
import contract.searchRequests.RuleSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static contract.rules.enums.RuleRequestCategory.ANY_RULE_TYPE;
import static contract.rules.enums.RuleSource.ANY_DOCUMENT;
import static java.util.Collections.emptyList;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/search")
    public List<ApiNormalizedRule> search(
            @RequestParam(value="keywords", required = false) List<String> keywords,
            @RequestParam(value="ruleSource", required = false) RuleSource ruleSource,
            @RequestParam(value="pageNumber", required = false) Integer pageNumber,
            @RequestParam(value="ruleRequestCategory", required = false) RuleRequestCategory ruleRequestCategory
    ) {
        RuleSearchRequest ruleSearchRequest = new RuleSearchRequest(
                keywords == null ? emptyList() : keywords,
                ruleSource == null ? ANY_DOCUMENT : ruleSource,
                pageNumber == null ? 0 : pageNumber,
                ruleRequestCategory == null ? ANY_RULE_TYPE : ruleRequestCategory
        );
        if (ruleSearchRequest.getKeywords().size() == 0) {
            return emptyList();
        }
        return searchService.getRuleSearchResults(ruleSearchRequest);
    }
}
