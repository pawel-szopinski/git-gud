package pl.pawelszopinski.result;

import pl.pawelszopinski.parsedentity.ParsedResult;

import java.util.List;

public interface ParsableResult {

    <T extends ParsedResult> List<ParsedResult> compileParsedResult(Class<T> type)
            throws Exception;
}
