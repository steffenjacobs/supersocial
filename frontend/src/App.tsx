import React from 'react';
import { Sidebar } from './Sidebar';
import { AboutPage } from './AboutPage';
import { MessageOverview } from './MessageOverview';
import { EventBus } from './EventBus';

function App() {
  const eventBus = new EventBus();
  const dummyLoginInfo = {username: "Steffen"};
  const components = [{
    title: 'Message Overview',
    page: <MessageOverview eventBus = {eventBus}/>,
    selected: true,
    id: 0,
    icon: <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
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
   </svg>
  }, {
    id: 1,
    title: 'About',
    page: <AboutPage/>,
    icon: <svg version="1.0" xmlns="http://www.w3.org/2000/svg"
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
   </svg>
  }];
  return (
    <div className="App">
      <Sidebar components = {components} eventBus={eventBus} currentUser={dummyLoginInfo}/>
    </div>
  );
}

export default App;
