# SubstringCompression

Based on a StackExchange question I submitted in April 2016. The original goal was to apply "Compression by Substring Enumeration" concepts to the English Braille alphabet as an experiment in lossless data compression. Below is the original question:

-----


Background:

The English Braille system is laid out in such a way so that the letters can be referenced by their position in the alphabet. Of the six dots available for each character, the top four are used to indicate the numbers 1 through 10, and the bottom two are used to distinguish between groups of ten. (Grade 1 Braille)

Since there only 26 letters and 64 (2^6) possible combination of raised/flat dots, this leaves plenty of space for contractions (abbreviations): single characters that represent common sequences of letters in English text. (Grade 2 Braille)

See the chart here: https://en.wikipedia.org/wiki/English_Braille#Alphabet

While the existing system makes it easy to learn and read Braille text, it is definitely not the most efficient use of dots. The most common letters- ETAOIN- require 2, 4, 1, 3, 2, and 4 dots respectively.

Proposing a system to reduce the number of dots in printed Braille would be incredibly ambiguous and confusing for touch-readers, and less intuitive to learn, but it does make for an interesting math problem, and may have useful applications in the field of data compression or cryptography. I'm only approaching this from a theoretical standpoint; trying to alter the current Braille standards is impractical.

If we were to minimize the number of dots used by Grade 1 Braille (one-for-one substitution of English letters with Braille characters), then the obvious solution is to arrange the letters by frequency and assign characters with fewer dots to common letters:

Dots   Letters
 0     (space)
 1     ETAOI N
 2     SRHLD CUMFP GWYBV
 3     KXJQZ
 4     (unused)
 5     (unused)
 6     (unused)
However, the contractions available through Grade 2 Braille allow us to greatly reduce the number of dots used by replacing common sequences of letters with a single character. For example, the word "handshake" requires (3+1+4+3+3+3+1+2+2=22) dots, but with contractions, it becomes "h(and)(sh)ake" which only requires (3+5+3+1+2+2=16) dots.

Sure, with the optimized single-letter system listed above, the same word completely spelled out only takes 15 dots, but given the frequency of letter sequences like "and" and "sh", why wouldn't we take advantage of them? If we assigned 3-dot characters to the sequences "and" and "sh", each contraction saves us one more dot.

Consider the following study on letter and n-gram frequency counts: http://norvig.com/mayzner.html

When you get into 4- and 5-letter contractions, and allow for the possibility of reserving some of those 1- and 2-dot characters for extremely common contractions such as "the", "of", and "and", this makes for a challenging optimization problem: reserving too many low-dot-count characters for contractions will make spelling out other less common words more expensive, but reserving too few will make all words slightly more expensive.

Constraints:

- Assume the reader can readily differentiate between all 64 possible Braille characters (i.e. the 6 single-dot characters might be difficult to tell apart simply by touch in reality, but we're limiting this just to theory)
- The 0-dot character must be used to separate words, and cannot be used to represent letters or words.
- Contractions (multiple English letters that evaluate to a single Braille character) are evaluated from longest sequence to shortest, left to right. If you had three characters in your solution that represented AB, CDE, DEF, and BCDE, then the word ABCDE would be transcribed as A+(BCDE), not (AB)+(CDE), even if the latter contained fewer dots. And the word CDEF would always be written as (CDE)+F even though C+(DEF) is also legal. This rule also disregards pronunciation and legibility: in standard English Braille the "th" in "outhouse" is spelled out ("t"+"h") because it represents two distinct sounds, but for this experiment, we're focusing only on spelling.
- Contractions cannot span words. If you have contractions for the sequences AB, CDE, and BCDE, and two adjacent words are "XAB CDEF", they must be evaluated as "X(AB) (CDE)F", not "XA(B CDE)F".
- You may disregard case and punctuation, and assume that no words have special characters (e.g., á, ç, Ü, ñ). In other words, our universe consists of the standard Latin A-Z and space. In fact, the frequency study referenced above omitted words with special characters.
- The optimal solution is the one which uses the fewest total dots to represent all text. This can be computed by summing the products of the "cost" (number of dots) per word/letter times the frequency of the word/letter. (Note: the frequency of words and letters change based on the contractions selected. Replacing all instances of "the" with a single character will reduce the frequency of the letters T, H, and E, as well as the contractions "TH" and "HE", in the remaining text.

Questions:

Is there a better approach to this problem than simple brute-force or guess-and-check?

Given the study and data (Google books Ngrams, English 20120701 1-grams) linked above, what is the optimal mapping of Braille characters to English letters and letter sequences?

-----

Update, August 9th, 2016:
There is a process called "Compression by Substring Enumeration" (CSE) that is similar if not identical to the goals of this project. It may be worthwhile to investigate the studies done on this subject.