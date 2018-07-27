/*
Copyright 2018 Penny Rohr Curich

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package guru.qas.martini.intellij.plugin;

import org.jetbrains.annotations.NotNull;

public class RegEx {

	private static final String PREFIX_CHAR = "^";
	private static final String SUFFIX_CHAR = "$";
	private static final char ESCAPE_SLASH = '\\';
	private static final char LEFT_PAR = '(';
	private static final char RIGHT_PAR = ')';

	private static final char LEFT_BRACE = '{';
	private static final char RIGHT_BRACE = '}';
	private static final char LEFT_SQUARE_BRACE = '[';
	private static final char RIGHT_SQUARE_BRACE = ']';

	private RegEx() {
	}

	public static String getTheBiggestWordToSearchByIndex(@NotNull String regexp) {
		String result = "";
		if (regexp.startsWith(PREFIX_CHAR)) {
			regexp = regexp.substring(1);
		}
		if (regexp.endsWith(SUFFIX_CHAR)) {
			regexp = regexp.substring(0, regexp.length() - 1);
		}

		int par = 0;
		int squareBrace = 0;
		int brace = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < regexp.length(); i++) {
			char c = regexp.charAt(i);
			if (c == '#') {
				sb = new StringBuilder();
				continue;
			}
			if (c != ESCAPE_SLASH) {
				if (c == LEFT_PAR) {
					par++;
				}
				if (c == RIGHT_PAR) {
					if (par > 0) {
						par--;
					}
				}

				if (c == LEFT_BRACE) {
					brace++;
				}
				if (c == RIGHT_BRACE) {
					if (brace > 0) {
						brace--;
					}
				}

				if (c == LEFT_SQUARE_BRACE) {
					squareBrace++;
				}
				if (c == RIGHT_SQUARE_BRACE) {
					if (squareBrace > 0) {
						squareBrace--;
					}
				}
			}
			else {
				sb = new StringBuilder();
				i++;
			}
			if (par > 0 | squareBrace > 0 | brace > 0) {
				if (par + squareBrace + brace == 1) {
					// if it's first brace
					sb = new StringBuilder();
				}
				continue;
			}
			if (Character.isLetterOrDigit(c)) {
				sb.append(c);
				if (sb.length() > 0) {
					if (sb.toString().length() > result.length()) {
						result = sb.toString();
					}
				}
			}
			else {
				sb = new StringBuilder();
			}
		}
		if (sb.length() > 0) {
			if (sb.toString().length() > result.length()) {
				result = sb.toString();
			}
		}
		return result.trim();
	}
}
