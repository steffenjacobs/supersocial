import React from "react";

export class ImageProvider {

    static getImage(id: string) :JSX.Element {
        switch (id) {

            //Facebook Logo
            case "facebook-logo":
                return <svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                    width="1.5em" height="1.5em" viewBox="0 0 266.893 266.895">
                    <path id="Blue_1_" fill="#3C5A99" d="M248.082,262.307c7.854,0,14.223-6.369,14.223-14.225V18.812
	c0-7.857-6.368-14.224-14.223-14.224H18.812c-7.857,0-14.224,6.367-14.224,14.224v229.27c0,7.855,6.366,14.225,14.224,14.225
	H248.082z"/>
                    <path id="f" fill="#FFFFFF" d="M182.409,262.307v-99.803h33.499l5.016-38.895h-38.515V98.777c0-11.261,3.127-18.935,19.275-18.935
	l20.596-0.009V45.045c-3.562-0.474-15.788-1.533-30.012-1.533c-29.695,0-50.025,18.126-50.025,51.413v28.684h-33.585v38.895h33.585
	v99.803H182.409z"/>
                </svg>;

            //Twitter Logo
            case "twitter-logo": return <svg width="1.5em" height="1.5em" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                viewBox="0 0 210 279.9">
                <path fill="#157DC3" d="M165.5,268.2H94.3l-1.5-0.1c-48.4-4.4-80.8-40.8-80.5-90.3V41.8c0-17.7,14.3-32,32-32s32,14.3,32,32v47.2
                   l92.9,0.9c17.7,0.2,31.9,14.6,31.7,32.3c-0.2,17.6-14.5,31.7-32,31.7c-0.1,0-0.2,0-0.3,0L76.3,153v24.9
                   c-0.1,22.7,14.1,25.6,21,26.3h68.2c17.7,0,32,14.3,32,32S183.2,268.2,165.5,268.2z"/>
            </svg>;

            //X Logo
            case "none-logo": return <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
                width="1.5em" height="1.5em" viewBox="0 0 1280.000000 1280.000000">
                <g transform="translate(0.000000,1280.000000) scale(0.100000,-0.100000)"
                    fill="#d94f4f">
                    <path d="M1327 11473 l-1327 -1328 1872 -1872 1873 -1873 -1873 -1873 -1872
       -1872 1327 -1328 1328 -1327 1872 1872 1873 1873 1873 -1873 1872 -1872 1328
       1327 1327 1328 -1872 1872 -1873 1873 1873 1873 1872 1872 -1327 1328 -1328
       1327 -1872 -1872 -1873 -1873 -1873 1873 -1872 1872 -1328 -1327z"/>
                </g>
            </svg>;

            //Refresh Icon
            case "refresh": return <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 32 32" version="1.1" width="1.5em" fill="#545c60">
                <g><path d="M 16 4 C 10.886719 4 6.617188 7.160156 4.875 11.625 L 6.71875 12.375 C 8.175781 8.640625 11.710938 6 16 6 C 19.242188 6 22.132813 7.589844 23.9375 10 L 20 10 L 20 12 L 27 12 L 27 5 L 25 5 L 25 8.09375 C 22.808594 5.582031 19.570313 4 16 4 Z M 25.28125 19.625 C 23.824219 23.359375 20.289063 26 16 26 C 12.722656 26 9.84375 24.386719 8.03125 22 L 12 22 L 12 20 L 5 20 L 5 27 L 7 27 L 7 23.90625 C 9.1875 26.386719 12.394531 28 16 28 C 21.113281 28 25.382813 24.839844 27.125 20.375 Z " /></g>
            </svg>;

            //Home Icon
            case "home-icon": return <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
                width="1em" height="1em" viewBox="0 0 1280.000000 1206.000000"
                preserveAspectRatio="xMidYMid meet">
                <metadata>
                    Created by potrace 1.15, written by Peter Selinger 2001-2017
            </metadata>
                <g transform="translate(0.000000,1206.000000) scale(0.100000,-0.100000)" className="navbar-icon"
                    stroke="none">
                    <path d="M5828 11600 c-282 -250 -1418 -1256 -2523 -2235 -1106 -978 -2165
 -1917 -2355 -2085 -190 -168 -460 -407 -600 -530 -140 -124 -276 -245 -302
 -270 l-48 -45 330 -370 c181 -203 336 -369 343 -369 8 1 603 521 1323 1156
 3778 3332 4388 3868 4395 3866 4 -2 470 -413 1036 -914 565 -501 1842 -1631
 2836 -2512 994 -881 1812 -1602 1817 -1602 5 0 12 5 15 10 5 8 11 7 21 -1 19
 -15 -10 -45 339 351 153 173 293 331 312 351 l34 36 -838 739 c-461 407 -1544
 1363 -2408 2124 -863 762 -1919 1693 -2347 2070 l-776 685 -45 0 -45 0 -514
 -455z"/>
                    <path d="M5707 9301 c-364 -322 -1370 -1213 -2235 -1979 l-1572 -1393 2 -2872
 3 -2872 22 -41 c12 -23 41 -58 64 -77 83 -72 -24 -67 1561 -67 l1428 0 2 1273
 c3 1266 3 1272 24 1317 26 58 76 108 134 134 45 21 52 21 1224 24 1304 3 1248
 5 1322 -60 52 -46 81 -97 93 -163 8 -40 11 -454 11 -1292 l0 -1233 1484 2
 1485 3 30 29 c17 16 45 63 63 105 l33 76 2 2860 2 2860 -2240 1975 c-1270
 1120 -2249 1976 -2260 1976 -13 1 -247 -200 -682 -585z"/>
                </g>
            </svg>;

            //Info Icon
            case "info-icon": return <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
                width="1em" height="1em" viewBox="0 0 1280.000000 1280.000000"
                preserveAspectRatio="xMidYMid meet">
                <metadata>
                    Created by potrace 1.15, written by Peter Selinger 2001-2017
 </metadata>
                <g transform="translate(0.000000,1280.000000) scale(0.100000,-0.100000)" className="navbar-icon"
                    stroke="none">
                    <path d="M8175 12765 c-703 -114 -1248 -608 -1387 -1258 -17 -82 -21 -136 -22
 -277 0 -202 15 -307 70 -470 149 -446 499 -733 1009 -828 142 -26 465 -23 619
 6 691 131 1201 609 1328 1244 31 158 31 417 0 565 -114 533 -482 889 -1038
 1004 -133 27 -448 35 -579 14z"/>
                    <path d="M7070 9203 c-212 -20 -275 -27 -397 -48 -691 -117 -1400 -444 -2038
 -940 -182 -142 -328 -270 -585 -517 -595 -571 -911 -974 -927 -1181 -6 -76 11
 -120 69 -184 75 -80 159 -108 245 -79 109 37 263 181 632 595 539 606 774 826
 1035 969 135 75 231 105 341 106 82 1 94 -2 138 -27 116 -68 161 -209 122
 -376 -9 -36 -349 -868 -757 -1850 -407 -982 -785 -1892 -838 -2021 -287 -694
 -513 -1389 -615 -1889 -70 -342 -90 -683 -52 -874 88 -440 381 -703 882 -792
 124 -23 401 -30 562 -16 783 69 1674 461 2561 1125 796 596 1492 1354 1607
 1751 43 146 -33 308 -168 360 -61 23 -100 15 -173 -36 -105 -74 -202 -170
 -539 -529 -515 -551 -762 -783 -982 -927 -251 -164 -437 -186 -543 -65 -56 64
 -74 131 -67 247 13 179 91 434 249 815 135 324 1588 4102 1646 4280 106 325
 151 561 159 826 9 281 -22 463 -112 652 -58 122 -114 199 -211 292 -245 233
 -582 343 -1044 338 -91 -1 -181 -3 -200 -5z"/>
                </g>
            </svg>;
            default: return <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
            width="1.5em" height="1.5em" viewBox="0 0 1280.000000 1280.000000">
            <g transform="translate(0.000000,1280.000000) scale(0.100000,-0.100000)"
                fill="#ddd">
                <path d="M1327 11473 l-1327 -1328 1872 -1872 1873 -1873 -1873 -1873 -1872
   -1872 1327 -1328 1328 -1327 1872 1872 1873 1873 1873 -1873 1872 -1872 1328
   1327 1327 1328 -1872 1872 -1873 1873 1873 1873 1872 1872 -1327 1328 -1328
   1327 -1872 -1872 -1873 -1873 -1873 1873 -1872 1872 -1328 -1327z"/>
            </g>
        </svg>;
        }
    }
}