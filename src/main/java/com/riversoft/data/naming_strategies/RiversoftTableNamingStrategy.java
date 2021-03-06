package com.riversoft.data.naming_strategies;

import com.riversoft.resolvers.SpellExclusionResolver;
import org.hibernate.cfg.ImprovedNamingStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RiversoftTableNamingStrategy extends ImprovedNamingStrategy {

    private static final long serialVersionUID = 1L;
    private final TransformerToPluralForm transformerToPluralForm = new TransformerToPluralForm();

    public RiversoftTableNamingStrategy() {
        super();
        this.initializeSpellExclusionResolver();
    }

    protected SpellExclusionResolver spellExclusionResolver;

    protected void initializeSpellExclusionResolver() {
        this.spellExclusionResolver = null;
    }

    @Override
    public String classToTableName(String className) {
        String tableNameInSingularForm = super.classToTableName(className);
        return transformToPluralForm(tableNameInSingularForm);
    }

    public String transformToPluralForm(String tableNameInSingularForm) {

        if (spellExclusionResolver != null) {
            String result = spellExclusionResolver.resolve(tableNameInSingularForm);
            if (result != null) {
                return result;
            }
        }

        return transformerToPluralForm.transform(tableNameInSingularForm);
    }

    public static class TransformerToPluralForm {

        private static final Pattern PATTERN_ES = Pattern.compile("(s|ss|sh|ch|x|z|o)$", Pattern.CASE_INSENSITIVE);
        private static final Pattern PATTERN_S = Pattern.compile("[aeiou]y$", Pattern.CASE_INSENSITIVE);
        private static final Pattern PATTERN_IES = Pattern.compile("y$", Pattern.CASE_INSENSITIVE);
        private static final Pattern PATTERN_VES = Pattern.compile("(f|fe)$", Pattern.CASE_INSENSITIVE);

        private static final String SUFFIX_S = "s";
        private static final String SUFFIX_IES = "ies";
        private static final String SUFFIX_ES = "es";
        private static final String SUFFIX_VES = "ves";

        public String transform(String word) {
            if (PATTERN_ES.matcher(word).find()) {
                return word + SUFFIX_ES;
            } else if (PATTERN_S.matcher(word).find()) {
                return word + SUFFIX_S;
            } else if (PATTERN_IES.matcher(word).find()) {
                return word.substring(0, word.length() - 1) + SUFFIX_IES;
            }

            Matcher matcher = PATTERN_VES.matcher(word);
            if (matcher.find()) {
                return matcher.replaceAll(SUFFIX_VES);
            }

            return word + SUFFIX_S;
        }
    }
}

