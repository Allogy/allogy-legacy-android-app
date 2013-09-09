/*
 * Copyright (c) 2013 Allogy Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hsl.txtreader;

/**
 * some constants and utility functions for font support.
 * @author Mike Wessler
 */
public class FontSupport {

    /**
     * names for glyphs in the standard Adobe order.  This is the ordering
     * of the glyphs in a font, not the mapping of character number to
     * character.
     */
    public static final String stdNames[] = {
        ".notdef", "space", "exclam", "quotedbl", "numbersign", "dollar",
        "percent", "ampersand", "quoteright", "parenleft", "parenright",
        "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero",
        "one", "two", "three", "four", "five", "six", "seven", "eight",
        "nine", "colon", "semicolon", "less", "equal", "greater", "question",
        "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "bracketleft", "backslash", "bracketright", "asciicircum",
        "underscore", "quoteleft", "a", "b", "c", "d", "e", "f", "g", "h",
        "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
        "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde",
        "exclamdown", "cent", "sterling", "fraction", "yen", "florin",
        "section", "currency", "quotesingle", "quotedblleft", "guillemotleft",
        "guilsinglleft", "guilsinglright", "fi", "fl", "endash", "dagger",
        "daggerdbl", "periodcentered", "paragraph", "bullet",
        "quotesinglbase", "quotedblbase", "quotedblright", "guillemotright",
        "ellipsis", "perthousand", "questiondown", "grave", "acute",
        "circumflex", "tilde", "macron", "breve", "dotaccent", "dieresis",
        "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "emdash", "AE",
        "ordfeminine", "Lslash", "Oslash", "OE", "ordmasculine", "ae",
        "dotlessi", "lslash", "oslash", "oe", "germandbls", "onesuperior",
        "logicalnot", "mu", "trademark", "Eth", "onehalf", "plusminus",
        "Thorn", "onequarter", "divide", "brokenbar", "degree", "thorn",
        "threequarters", "twosuperior", "registered", "minus", "eth",
        "multiply", "threesuperior", "copyright", "Aacute", "Acircumflex",
        "Adieresis", "Agrave", "Aring", "Atilde", "Ccedilla", "Eacute",
        "Ecircumflex", "Edieresis", "Egrave", "Iacute", "Icircumflex",
        "Idieresis", "Igrave", "Ntilde", "Oacute", "Ocircumflex", "Odieresis",
        "Ograve", "Otilde", "Scaron", "Uacute", "Ucircumflex", "Udieresis",
        "Ugrave", "Yacute", "Ydieresis", "Zcaron", "aacute", "acircumflex",
        "adieresis", "agrave", "aring", "atilde", "ccedilla", "eacute",
        "ecircumflex", "edieresis", "egrave", "iacute", "icircumflex",
        "idieresis", "igrave", "ntilde", "oacute", "ocircumflex", "odieresis",
        "ograve", "otilde", "scaron", "uacute", "ucircumflex", "udieresis",
        "ugrave", "yacute", "ydieresis", "zcaron", "exclamsmall",
        "Hungarumlautsmall", "dollaroldstyle", "dollarsuperior",
        "ampersandsmall", "Acutesmall", "parenleftsuperior",
        "parenrightsuperior", "twodotenleader", "onedotenleader",
        "zerooldstyle", "oneoldstyle", "twooldstyle", "threeoldstyle",
        "fouroldstyle", "fiveoldstyle", "sixoldstyle", "sevenoldstyle",
        "eightoldstyle", "nineoldstyle", "commasuperior",
        "threequartersemdash", "periodsuperior", "questionsmall", "asuperior",
        "bsuperior", "centsuperior", "dsuperior", "esuperior", "isuperior",
        "lsuperior", "msuperior", "nsuperior", "osuperior", "rsuperior",
        "ssuperior", "tsuperior", "ff", "ffi", "ffl", "parenleftinferior",
        "parenrightinferior", "Circumflexsmall", "hyphensuperior",
        "Gravesmall", "Asmall", "Bsmall", "Csmall", "Dsmall", "Esmall",
        "Fsmall", "Gsmall", "Hsmall", "Ismall", "Jsmall", "Ksmall", "Lsmall",
        "Msmall", "Nsmall", "Osmall", "Psmall", "Qsmall", "Rsmall", "Ssmall",
        "Tsmall", "Usmall", "Vsmall", "Wsmall", "Xsmall", "Ysmall", "Zsmall",
        "colonmonetary", "onefitted", "rupiah", "Tildesmall",
        "exclamdownsmall", "centoldstyle", "Lslashsmall", "Scaronsmall",
        "Zcaronsmall", "Dieresissmall", "Brevesmall", "Caronsmall",
        "Dotaccentsmall", "Macronsmall", "figuredash", "hypheninferior",
        "Ogoneksmall", "Ringsmall", "Cedillasmall", "questiondownsmall",
        "oneeighth", "threeeighths", "fiveeighths", "seveneighths",
        "onethird", "twothirds", "zerosuperior", "foursuperior",
        "fivesuperior", "sixsuperior", "sevensuperior", "eightsuperior",
        "ninesuperior", "zeroinferior", "oneinferior", "twoinferior",
        "threeinferior", "fourinferior", "fiveinferior", "sixinferior",
        "seveninferior", "eightinferior", "nineinferior", "centinferior",
        "dollarinferior", "periodinferior", "commainferior", "Agravesmall",
        "Aacutesmall", "Acircumflexsmall", "Atildesmall", "Adieresissmall",
        "Aringsmall", "AEsmall", "Ccedillasmall", "Egravesmall",
        "Eacutesmall", "Ecircumflexsmall", "Edieresissmall", "Igravesmall",
        "Iacutesmall", "Icircumflexsmall", "Idieresissmall", "Ethsmall",
        "Ntildesmall", "Ogravesmall", "Oacutesmall", "Ocircumflexsmall",
        "Otildesmall", "Odieresissmall", "OEsmall", "Oslashsmall",
        "Ugravesmall", "Uacutesmall", "Ucircumflexsmall", "Udieresissmall",
        "Yacutesmall", "Thornsmall", "Ydieresissmall", "001.000", "001.001",
        "001.002", "001.003", "Black", "Bold", "Book", "Light", "Medium",
        "Regular", "Roman", "Semibold"
    };

    /**
     * characters for glyphs in the standard order.  These are string "values"
     * to go with the names in stdNames.  Not all glyphs have been translated
     * to their unicode values.  In many cases, the name of the glyph has
     * been appended to an ASCII approximation of the glyph.  Strings longer
     * than 3 characters have this characteristic.  To get the character,
     * use the string if it contains 3 or fewer characters; otherwise,
     * grab the first character off the string and use that.
     */
    public static final String stdValues[] = {
        "", " ", "!", "\"", "#", "$",
        "%", "&", "'", "(", ")",
        "*", "+", ",", "-", ".", "/", "0",
        "1", "2", "3", "4", "5", "6", "7", "8",
        "9", ":", ";", "<", "=", ">", "?",
        "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "[", "\\", "]", "^",
        "_", "`", "a", "b", "c", "d", "e", "f", "g", "h",
        "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
        "w", "x", "y", "z", "{", "|", "}", "~",
        "\u00a1", "\u00a2", "\u00a3", "\u2044", "\u00a5", "\u0192", "\u00a7", "\u00a4", "\u00b4", "\u201c", "\u00ab", "\u2039", "\u203a", "\ufb01", "\ufb02", "\u2013",
        "\u2020", "\u2021", "\u00b7", "\u00b6", "\u2022", "\u201a", "\u201e", "\u201d", "\u00bb", "\u2026", "\u2030", "\u00bf", "\u0060", "\u00b4", "\u02c6", "\u02dc",

        "\u00af", //128
        "\u02d8",
        "\u02d9",
        "\u00a8",
        "\u02da",
        "\u00b8",
        "\u02dd",
        "\u02db",
        "\u02c7",
        "\u2014",
        "\u00c6",
        "\u00aa",
        "\u0141",
        "\u00d8",
        "\u0152",
        "\u00ba", //143

        "\u00e6", //144
        "\u0131",
        "\u0142",
        "\u00f8",
        "\u0153",
        "\u00df",
        "\u00b9",
        "\u00ac",
        "\u00b5",
        "\u2122",
        "\u00d0",
        "\u00bd",
        "\u00b1",
        "\u00de",
        "\u00bc",
        "\u00f7", //159

        "\u00a6", //160
        "\u00b0",
        "\u00fe",
        "\u00be",
        "\u00b2",
        "\u00ae",
        "\u2212",
        "\u00f0",
        "\u00d7",
        "\u00b3",
        "\u00a9",
        "\u00c1",
        "\u00c2",
        "\u00c4",
        "\u00c0",
        "\u00c5", //175

        "\u00c3", //176
        "\u00c7",
        "\u00c9",
        "\u00ca",
        "\u00cb",
        "\u00c8",
        "\u00cd",
        "\u00ce",
        "\u00cf",
        "\u00cc",
        "\u00d1",
        "\u00d3",
        "\u00d4",
        "\u00d6",
        "\u00d2",
        "\u00d5", //191

        "\u0160", //192
        "\u00da",
        "\u00db",
        "\u00dc",
        "\u00d9",
        "\u00dd",
        "\u0178",
        "\u017d",
        "\u00e1",
        "\u00e2",
        "\u00e4",
        "\u00e0",
        "\u00e5",
        "\u00e3",
        "\u00e7",
        "\u00e9", //207

        "\u00ea", //208
        "\u00eb",
        "\u00e8",
        "\u00ed",
        "\u00ee",
        "\u00ef",
        "\u00ec",
        "\u00f1",
        "\u00f3",
        "\u00f4",
        "\u00f6",
        "\u00f2",
        "\u00f5",
        "\u0161",
        "\u00fa",
        "\u00fb", //223

        "\u00fc", //224
        "\u00f9",
        "\u00fd",
        "\u00ff",
        "\u017e",
        "\u2025",
        "\u2024",
        "\u20a1",
        "\u2012",
        "\u215b",
        "\u215c",
        "\u215d",
        "\u215e",
        "\u2153",
        "\u2154", //238 twothirds

        "\u2070", //239 zerosuperior
        "\u2074", //240 foursuperior
        "\u2075",
        "\u2076",
        "\u2077",
        "\u2078",
        "\u2079", //245 ninesuperior

        "\u2080", //246 zeroinferior
        "\u2081",
        "\u2082",
        "\u2083",
        "\u2084",
        "\u2085",
        "\u2086",
        "\u2087",
        "\u2088",
        "\u2089", //255 nineinferior

        "ccentinferior",
        "$dollarinferior", ".periodinferior", ",commainferior", "AAgravesmall",
        "AAacutesmall", "AAcircumflexsmall", "AAtildesmall", "AAdieresissmall",
        "AAringsmall", "AEAEsmall", "CCcedillasmall", "EEgravesmall",
        "EEacutesmall", "EEcircumflexsmall", "EEdieresissmall", "IIgravesmall",
        "IIacutesmall", "IIcircumflexsmall", "IIdieresissmall", "EthEthsmall",
        "NNtildesmall", "OOgravesmall", "OOacutesmall", "OOcircumflexsmall",
        "OOtildesmall", "OOdieresissmall", "OEOEsmall", "OOslashsmall",
        "UUgravesmall", "UUacutesmall", "UUcircumflexsmall", "UUdieresissmall",
        "YYacutesmall", "?Thornsmall", "YYdieresissmall", "?001.000", "?001.001",
        "?001.002", "?001.003", " Black", " Bold", " Book", " Light", " Medium",
        " Regular", " Roman", " Semibold",
        /* extra mac stuff */
        "?NUL", "?HT", " LF", " CR", "?DLE", "?DC1", "?DC2", "?DC3", "?DC4", "?RS",
        "?US", "!=", "?DEL", "?infinity", "<=", ">=",
        "?partialdiff", "?summation", "xproduct", "?pi", "?integral", "?Omega",
        "?radical", "~=", "?Delta", " nbspace", "?lozenge", "?apple"
    };

    /**
     * glyph order of the glyphs for the Type1C Expert character set.  These
     * are indices into the glyph name array.
     */
    public static final int type1CExpertCharset[] = {
        1, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 13, 14, 15, 99,
        239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 27, 28, 249, 250,
        251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264,
        265, 266, 109, 110, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276,
        277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290,
        291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304,
        305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318,
        158, 155, 163, 319, 320, 321, 322, 323, 324, 325, 326, 150, 164, 169,
        327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340,
        341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354,
        355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367, 368,
        369, 370, 371, 372, 373, 374, 375, 376, 377, 378
    };

    /**
     * glyph order of the glyphs for the Type1C Expert Sub character set.
     * These are indices into the glyph name array.
     */
    public static final int type1CExpertSubCharset[] = {
        1, 231, 232, 235, 236, 237, 238, 13, 14, 15, 99, 239, 240, 241, 242,
        243, 244, 245, 246, 247, 248, 27, 28, 249, 250, 251, 253, 254, 255,
        256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 109, 110, 267,
        268, 269, 270, 272, 300, 301, 302, 305, 314, 315, 158, 155, 163, 320,
        321, 322, 323, 324, 325, 326, 150, 164, 169, 327, 328, 329, 330, 331,
        332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345,
        346
    };

    /**
     * extra names for the Macintosh glyph set.  This array should be
     * considered to be appended to the stdNames array.  The stdValues array
     * already contains values for this set.
     */
    public static final String macExtras[] = { // index starts at 391=NUL
        "NUL", "HT", "LF", "CR", "DLE", "DC1", "DC2", "DC3", "DC4", "RS",
        "US", "notequal", "DEL", "infinity", "lessequal", "greaterequal",
        "partialdiff", "summation", "product", "pi", "integral", "Omega",
        "radical", "approxequal", "Delta", "nbspace", "lozenge", "apple"
    };

    /**
     * character mapping from values to glyphs for the Macintosh MacRoman
     * encoding
     */
    public static final int macRomanEncoding[] = {
        391, 154, 167, 140, 146, 192, 221, 197, 226, 392, 393, 157, 162, 394,
        199, 228, 395, 396, 397, 398, 399, 155, 158, 150, 163, 169, 164, 160,
        166, 168, 400, 401, 1, 2, 3, 4, 5, 6, 7, 104, 9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
        32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
        49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 124,
        66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82,
        83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 403, 173, 175,
        177, 178, 186, 189, 195, 200, 203, 201, 202, 205, 204, 206, 207, 210,
        208, 209, 211, 214, 212, 213, 215, 216, 219, 217, 218, 220, 222, 225,
        223, 224, 112, 161, 97, 98, 102, 116, 115, 149, 165, 170, 153, 125,
        131, 402, 138, 141, 404, 156, 405, 406, 100, 152, 407, 408, 409, 410,
        411, 139, 143, 412, 144, 147, 123, 96, 151, 413, 101, 414, 415, 106,
        120, 121, 416, 174, 176, 191, 142, 148, 111, 137, 105, 119, 65, 8,
        159, 417, 227, 198, 99, 103, 107, 108, 109, 110, 113, 114, 117, 118,
        122, 172, 179, 171, 180, 181, 182, 183, 184, 185, 187, 188, 418, 190,
        193, 194, 196, 145, 126, 127, 128, 129, 130, 132, 133, 134, 135, 136
    };

    /**
     * character mapping from values to glyphs for the isoLatin1Encoding
     */
    public static final int isoLatin1Encoding[] = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        166, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64,
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
        82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 145, 124, 125, 126, 127, 128,
        129, 130, 131, 0, 132, 133, 0, 134, 135, 136, 1, 96, 97, 98, 103,
        100, 160, 102, 131, 170, 139, 106, 151, 14, 165, 128, 161, 156, 164,
        169, 125, 152, 115, 114, 133, 150, 143, 120, 158, 155, 163, 123, 174,
        171, 172, 176, 173, 175, 138, 177, 181, 178, 179, 180, 185, 182, 183,
        184, 154, 186, 190, 187, 188, 191, 189, 168, 141, 196, 193, 194, 195,
        197, 157, 149, 203, 200, 201, 205, 202, 204, 144, 206, 210, 207, 208,
        209, 214, 211, 212, 213, 167, 215, 219, 216, 217, 220, 218, 159, 147,
        225, 222, 223, 224, 226, 162, 227
    };

    /**
     * character mapping from values to glyphs for the Windows winAnsi
     * character encoding
     */
    public static final int winAnsiEncoding[] = {
        124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 145,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5,
        6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
        24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
        58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91,
        92, 93, 94, 95, 0, 0, 0, 117, 101, 118, 121, 112, 113, 0, 122, 192,
        107, 142, 0, 0, 0, 0, 65, 8, 105, 119, 116, 111, 137, 0, 153, 221,
        108, 148, 0, 0, 198, 1, 96, 97, 98, 103, 100, 160, 102, 131, 170,
        139, 106, 151, 14, 165, 128, 161, 156, 164, 169, 125, 152, 115, 114,
        133, 150, 143, 120, 158, 155, 163, 123, 174, 171, 172, 176, 173, 175,
        138, 177, 181, 178, 179, 180, 185, 182, 183, 184, 154, 186, 190, 187,
        188, 191, 189, 168, 141, 196, 193, 194, 195, 197, 157, 149, 203, 200,
        201, 205, 202, 204, 144, 206, 210, 207, 208, 209, 214, 211, 212, 213,
        167, 215, 219, 216, 217, 220, 218, 159, 147, 225, 222, 223, 224, 226,
        162, 227
    };

    /**
     * character mapping from values to glyphs for Adobe's standard
     * character encoding
     */
    public static final int standardEncoding[] = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64,
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81,
        82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105,
        106, 107, 108, 109, 110, 0, 111, 112, 113, 114, 0, 115, 116, 117,
        118, 119, 120, 121, 122, 0, 123, 0, 124, 125, 126, 127, 128, 129,
        130, 131, 0, 132, 133, 0, 134, 135, 136, 137, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 138, 0, 139, 0, 0, 0, 0, 140, 141, 142, 143,
        0, 0, 0, 0, 0, 144, 0, 0, 0, 145, 0, 0, 146, 147, 148, 149, 0, 0, 0,
        0
    };

    /**
     * get the name of a glyph from its encoding value (NOT the character
     * value), using the standard encoding.
     */
    public static String getName(int i) {
        if (i < stdNames.length) {
            return stdNames[i];
        } else {
            i -= stdNames.length;
            if (i < macExtras.length) {
                return macExtras[i];
            }
        }
        return ".notdef";
    }

    /**
     * get the encoding value a glyph given its name and a name table.
     * @param name the name of the glyph
     * @param table the charset as an array of names
     * @return the index of the name in the table, or -1 if the name
     * cannot be found in the table
     */
    public static int findName(String name, String[] table) {
        for (int i = 0; i < table.length; i++) {
            if (name.equals(table[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * get the encoding value of a glyph given its name and a charset.
     * @param name the name of the glyph
     * @param table the charset table
     * @return the index of the name in the charset.
     */
    public static int findName(String name, int[] table) {
        for (int i = 0; i < table.length; i++) {
            if (name.equals(getName(table[i]))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * get the encoding value of a glyph given its name, in the standard
     * charset.  This is equivalent to findName(name, FontSupport.stdNames).
     * @param name the name of the glyph
     * @return the index of the name in stdNames, or -1 if the name doesn't
     * appear in stdNames.
     */
    public static int getStrIndex(String name) {
        for (int i = 0; i < stdNames.length; i++) {
            if (name.equals(stdNames[i])) {
                return i;
            }
        }
        return -1;
    }
}
