package org.activehome.evaluator;

/*
 * #%L
 * Active Home :: Evaluator
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.activehome
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import org.activehome.evaluator.EvaluationReport;
import org.activehome.translator.Language;
import org.activehome.translator.Translator;
import org.kevoree.api.Callback;

/**
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public class EvaluationReportTranslator extends Translator<EvaluationReport> {

    public void translate(final EvaluationReport report,
                          final Language[] languages,
                          final Callback callback) {

    }

}
