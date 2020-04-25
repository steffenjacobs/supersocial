import React from "react";
import './DataPrivacyPage.css';
import './LandingPage.css';
import { Footer } from "./Footer";

export class DataPrivacyPage extends React.Component<any>{
    public render(){
        return (
        <div className="landing-site">
            <div className="landing-site-inner">
            <h2 className="landing-h2">Privacy Policy</h2>
            <div className="landing-box">
                <button onClick={e => window.location.href="/"} className="btn btn-primary landing-btn-header back-btn">&#9664; &nbsp; Go back</button>
        <p>We are very delighted that you have shown interest in our enterprise. Data protection is of a particularly high priority for the management of the a. The use of the Internet pages of the a is possible without any indication of personal data; however, if a data subject wants to use special enterprise services via our website, processing of personal data could become necessary. If the processing of personal data is necessary and there is no statutory basis for such processing, we generally obtain consent from the data subject.</p>
        
        <p>The processing of personal data, such as the name, address, e-mail address, or telephone number of a data subject shall always be in line with the General Data Protection Regulation (GDPR), and in accordance with the country-specific data protection regulations applicable to the a. By means of this data protection declaration, our enterprise would like to inform the general public of the nature, scope, and purpose of the personal data we collect, use and process. Furthermore, data subjects are informed, by means of this data protection declaration, of the rights to which they are entitled.</p>
        
        <p>As the controller, the a has implemented numerous technical and organizational measures to ensure the most complete protection of personal data processed through this website. However, Internet-based data transmissions may in principle have security gaps, so absolute protection may not be guaranteed. For this reason, every data subject is free to transfer personal data to us via alternative means, e.g. by telephone. </p>
        
        <h3 className="landing-h3">1. Definitions</h3>
        <p>The data protection declaration of the a is based on the terms used by the European legislator for the adoption of the General Data Protection Regulation (GDPR). Our data protection declaration should be legible and understandable for the general public, as well as our customers and business partners. To ensure this, we would like to first explain the terminology used.</p>
        
        <p>In this data protection declaration, we use, inter alia, the following terms:</p>
        
        <ul className="no-list">
        <li><h4 className="landing-h4">a)    Personal data</h4>
        <p>Personal data means any information relating to an identified or identifiable natural person (“data subject”). An identifiable natural person is one who can be identified, directly or indirectly, in particular by reference to an identifier such as a name, an identification number, location data, an online identifier or to one or more factors specific to the physical, physiological, genetic, mental, economic, cultural or social identity of that natural person.</p>
        </li>
        <li><h4 className="landing-h4">b) Data subject</h4>
        <p>Data subject is any identified or identifiable natural person, whose personal data is processed by the controller responsible for the processing.</p>
        </li>
        <li><h4 className="landing-h4">c)    Processing</h4>
        <p>Processing is any operation or set of operations which is performed on personal data or on sets of personal data, whether or not by automated means, such as collection, recording, organisation, structuring, storage, adaptation or alteration, retrieval, consultation, use, disclosure by transmission, dissemination or otherwise making available, alignment or combination, restriction, erasure or destruction. </p>
        </li>
        <li><h4 className="landing-h4">d)    Restriction of processing</h4>
        <p>Restriction of processing is the marking of stored personal data with the aim of limiting their processing in the future. </p>
        </li>
        <li><h4 className="landing-h4">e)    Profiling</h4>
        <p>Profiling means any form of automated processing of personal data consisting of the use of personal data to evaluate certain personal aspects relating to a natural person, in particular to analyse or predict aspects concerning that natural person's performance at work, economic situation, health, personal preferences, interests, reliability, behaviour, location or movements. </p>
        </li>
        <li><h4 className="landing-h4">f)     Pseudonymisation</h4>
        <p>Pseudonymisation is the processing of personal data in such a manner that the personal data can no longer be attributed to a specific data subject without the use of additional information, provided that such additional information is kept separately and is subject to technical and organisational measures to ensure that the personal data are not attributed to an identified or identifiable natural person. </p>
        </li>
        <li><h4 className="landing-h4">g)    Controller or controller responsible for the processing</h4>
        <p>Controller or controller responsible for the processing is the natural or legal person, public authority, agency or other body which, alone or jointly with others, determines the purposes and means of the processing of personal data; where the purposes and means of such processing are determined by Union or Member State law, the controller or the specific criteria for its nomination may be provided for by Union or Member State law. </p>
        </li>
        <li><h4 className="landing-h4">h)    Processor</h4>
        <p>Processor is a natural or legal person, public authority, agency or other body which processes personal data on behalf of the controller. </p>
        </li>
        <li><h4 className="landing-h4">i)      Recipient</h4>
        <p>Recipient is a natural or legal person, public authority, agency or another body, to which the personal data are disclosed, whether a third party or not. However, public authorities which may receive personal data in the framework of a particular inquiry in accordance with Union or Member State law shall not be regarded as recipients; the processing of those data by those public authorities shall be in compliance with the applicable data protection rules according to the purposes of the processing. </p>
        </li>
        <li><h4 className="landing-h4">j)      Third party</h4>
        <p>Third party is a natural or legal person, public authority, agency or body other than the data subject, controller, processor and persons who, under the direct authority of the controller or processor, are authorised to process personal data.</p>
        </li>
        <li><h4 className="landing-h4">k)    Consent</h4>
        <p>Consent of the data subject is any freely given, specific, informed and unambiguous indication of the data subject's wishes by which he or she, by a statement or by a clear affirmative action, signifies agreement to the processing of personal data relating to him or her. </p>
        </li>
        </ul>
        
        <h3 className="landing-h3">2. Name and Address of the controller</h3>
        <p>Controller for the purposes of the General Data Protection Regulation (GDPR), other data protection laws applicable in Member states of the European Union and other provisions related to data protection is:
        </p>
        
        <p>Steffen Jacobs</p>
        <p>Akademiestraße 4</p>
        <p>68159 Mannheim</p>
        <p>Deutschland</p>
        <p>Phone: 0172 6543444</p>
        <p>Email: steffen.jacobs@supersocial.cloud</p>
        <p>Website: https://www.supersocial.cloud</p>

        <h3 className="landing-h3">3. Name and Address of the Data Protection Officer</h3>
        <p>The Data Protection Officer of the controller is:</p>

        <p>Steffen Jacobs</p>
        <p>Akademiestraße 4</p>
        <p>68159 Mannheim</p>
        <p>Deutschland</p>
        <p>Phone: 0172 6543444</p>
        <p>Email: steffen.jacobs@supersocial.cloud</p>
        <p>Website: https://www.supersocial.cloud</p>

        <p>Any data subject may, at any time, contact our Data Protection Officer directly with all questions and suggestions concerning data protection.</p>
        
        <h3 className="landing-h3">3. Cookies</h3>
        <p>The Internet pages of the a use cookies. Cookies are text files that are stored in a computer system via an Internet browser.</p>
        
        <p>Many Internet sites and servers use cookies. Many cookies contain a so-called cookie ID. A cookie ID is a unique identifier of the cookie. It consists of a character string through which Internet pages and servers can be assigned to the specific Internet browser in which the cookie was stored. This allows visited Internet sites and servers to differentiate the individual browser of the dats subject from other Internet browsers that contain other cookies. A specific Internet browser can be recognized and identified using the unique cookie ID.</p>
        
        <p>Through the use of cookies, the a can provide the users of this website with more user-friendly services that would not be possible without the cookie setting.</p>
        
        <p>By means of a cookie, the information and offers on our website can be optimized with the user in mind. Cookies allow us, as previously mentioned, to recognize our website users. The purpose of this recognition is to make it easier for users to utilize our website. The website user that uses cookies, e.g. does not have to enter access data each time the website is accessed, because this is taken over by the website, and the cookie is thus stored on the user's computer system. Another example is the cookie of a shopping cart in an online shop. The online store remembers the articles that a customer has placed in the virtual shopping cart via a cookie.</p>
        
        <p>The data subject may, at any time, prevent the setting of cookies through our website by means of a corresponding setting of the Internet browser used, and may thus permanently deny the setting of cookies. Furthermore, already set cookies may be deleted at any time via an Internet browser or other software programs. This is possible in all popular Internet browsers. If the data subject deactivates the setting of cookies in the Internet browser used, not all functions of our website may be entirely usable.</p>
        
        <h3 className="landing-h3">4. Collection of general data and information</h3>
        <p>The website of the a collects a series of general data and information when a data subject or automated system calls up the website. This general data and information are stored in the server log files. Collected may be (1) the browser types and versions used, (2) the operating system used by the accessing system, (3) the website from which an accessing system reaches our website (so-called referrers), (4) the sub-websites, (5) the date and time of access to the Internet site, (6) an Internet protocol address (IP address), (7) the Internet service provider of the accessing system, and (8) any other similar data and information that may be used in the event of attacks on our information technology systems.</p>
        
        <p>When using these general data and information, the a does not draw any conclusions about the data subject. Rather, this information is needed to (1) deliver the content of our website correctly, (2) optimize the content of our website as well as its advertisement, (3) ensure the long-term viability of our information technology systems and website technology, and (4) provide law enforcement authorities with the information necessary for criminal prosecution in case of a cyber-attack. Therefore, the a analyzes anonymously collected data and information statistically, with the aim of increasing the data protection and data security of our enterprise, and to ensure an optimal level of protection for the personal data we process. The anonymous data of the server log files are stored separately from all personal data provided by a data subject.</p>
        
        <h3 className="landing-h3">5. Registration on our website</h3>
        <p>The data subject has the possibility to register on the website of the controller with the indication of personal data. Which personal data are transmitted to the controller is determined by the respective input mask used for the registration. The personal data entered by the data subject are collected and stored exclusively for internal use by the controller, and for his own purposes. The controller may request transfer to one or more processors (e.g. a parcel service) that also uses personal data for an internal purpose which is attributable to the controller.</p>
        
        <p>By registering on the website of the controller, the IP address—assigned by the Internet service provider (ISP) and used by the data subject—date, and time of the registration are also stored. The storage of this data takes place against the background that this is the only way to prevent the misuse of our services, and, if necessary, to make it possible to investigate committed offenses. Insofar, the storage of this data is necessary to secure the controller. This data is not passed on to third parties unless there is a statutory obligation to pass on the data, or if the transfer serves the aim of criminal prosecution.
        
        </p>
        
        <p>The registration of the data subject, with the voluntary indication of personal data, is intended to enable the controller to offer the data subject contents or services that may only be offered to registered users due to the nature of the matter in question. Registered persons are free to change the personal data specified during the registration at any time, or to have them completely deleted from the data stock of the controller.</p>
        
        <p>The data controller shall, at any time, provide information upon request to each data subject as to what personal data are stored about the data subject. In addition, the data controller shall correct or erase personal data at the request or indication of the data subject, insofar as there are no statutory storage obligations. The entirety of the controller’s employees are available to the data subject in this respect as contact persons.</p>
        
        <h3 className="landing-h3">6. Subscription to our newsletters</h3>
        <p>On the website of the a, users are given the opportunity to subscribe to our enterprise's newsletter. The input mask used for this purpose determines what personal data are transmitted, as well as when the newsletter is ordered from the controller.</p>
        
        <p>The a informs its customers and business partners regularly by means of a newsletter about enterprise offers. The enterprise's newsletter may only be received by the data subject if (1) the data subject has a valid e-mail address and (2) the data subject registers for the newsletter shipping. A confirmation e-mail will be sent to the e-mail address registered by a data subject for the first time for newsletter shipping, for legal reasons, in the double opt-in procedure. This confirmation e-mail is used to prove whether the owner of the e-mail address as the data subject is authorized to receive the newsletter.
        
        </p>
        
        <p>During the registration for the newsletter, we also store the IP address of the computer system assigned by the Internet service provider (ISP) and used by the data subject at the time of the registration, as well as the date and time of the registration. The collection of this data is necessary in order to understand the (possible) misuse of the e-mail address of a data subject at a later date, and it therefore serves the aim of the legal protection of the controller.</p>
        
        <p>The personal data collected as part of a registration for the newsletter will only be used to send our newsletter. In addition, subscribers to the newsletter may be informed by e-mail, as long as this is necessary for the operation of the newsletter service or a registration in question, as this could be the case in the event of modifications to the newsletter offer, or in the event of a change in technical circumstances. There will be no transfer of personal data collected by the newsletter service to third parties. The subscription to our newsletter may be terminated by the data subject at any time. The consent to the storage of personal data, which the data subject has given for shipping the newsletter, may be revoked at any time. For the purpose of revocation of consent, a corresponding link is found in each newsletter. It is also possible to unsubscribe from the newsletter at any time directly on the website of the controller, or to communicate this to the controller in a different way.</p>
        
        <h3 className="landing-h3">7. Newsletter-Tracking</h3>
        <p>The newsletter of the a contains so-called tracking pixels. A tracking pixel is a miniature graphic embedded in such e-mails, which are sent in HTML format to enable log file recording and analysis. This allows a statistical analysis of the success or failure of online marketing campaigns. Based on the embedded tracking pixel, the a may see if and when an e-mail was opened by a data subject, and which links in the e-mail were called up by data subjects.</p>
        
        <p>Such personal data collected in the tracking pixels contained in the newsletters are stored and analyzed by the controller in order to optimize the shipping of the newsletter, as well as to adapt the content of future newsletters even better to the interests of the data subject. These personal data will not be passed on to third parties. Data subjects are at any time entitled to revoke the respective separate declaration of consent issued by means of the double-opt-in procedure. After a revocation, these personal data will be deleted by the controller. The a automatically regards a withdrawal from the receipt of the newsletter as a revocation.</p>
        
        <h3 className="landing-h3">8. Contact possibility via the website </h3>
        <p>The website of the a contains information that enables a quick electronic contact to our enterprise, as well as direct communication with us, which also includes a general address of the so-called electronic mail (e-mail address). If a data subject contacts the controller by e-mail or via a contact form, the personal data transmitted by the data subject are automatically stored. Such personal data transmitted on a voluntary basis by a data subject to the data controller are stored for the purpose of processing or contacting the data subject. There is no transfer of this personal data to third parties.</p>
        
        <h3 className="landing-h3">9. Routine erasure and blocking of personal data</h3>
        <p>The data controller shall process and store the personal data of the data subject only for the period necessary to achieve the purpose of storage, or as far as this is granted by the European legislator or other legislators in laws or regulations to which the controller is subject to.</p>
        
        <p>If the storage purpose is not applicable, or if a storage period prescribed by the European legislator or another competent legislator expires, the personal data are routinely blocked or erased in accordance with legal requirements.</p>
        
        <h3 className="landing-h3">10. Rights of the data subject</h3>
        <ul className="no-list">
        <li><h4 className="landing-h4">a) Right of confirmation</h4>
        <p>Each data subject shall have the right granted by the European legislator to obtain from the controller the confirmation as to whether or not personal data concerning him or her are being processed. If a data subject wishes to avail himself of this right of confirmation, he or she may, at any time, contact any employee of the controller.</p>
        </li>
        <li><h4 className="landing-h4">b) Right of access</h4>
        <p>Each data subject shall have the right granted by the European legislator to obtain from the controller free information about his or her personal data stored at any time and a copy of this information. Furthermore, the European directives and regulations grant the data subject access to the following information:</p>
        
        <ul className="no-list">
        <li>the purposes of the processing;</li>
        <li>the categories of personal data concerned;</li>
        <li>the recipients or categories of recipients to whom the personal data have been or will be disclosed, in particular recipients in third countries or international organisations;</li>
        <li>where possible, the envisaged period for which the personal data will be stored, or, if not possible, the criteria used to determine that period;</li>
        <li>the existence of the right to request from the controller rectification or erasure of personal data, or restriction of processing of personal data concerning the data subject, or to object to such processing;</li>
        <li>the existence of the right to lodge a complaint with a supervisory authority;</li>
        <li>where the personal data are not collected from the data subject, any available information as to their source;</li>
        <li>the existence of automated decision-making, including profiling, referred to in Article 22(1) and (4) of the GDPR and, at least in those cases, meaningful information about the logic involved, as well as the significance and envisaged consequences of such processing for the data subject.</li>
        
        </ul>
        <p>Furthermore, the data subject shall have a right to obtain information as to whether personal data are transferred to a third country or to an international organisation. Where this is the case, the data subject shall have the right to be informed of the appropriate safeguards relating to the transfer.</p>
        
        <p>If a data subject wishes to avail himself of this right of access, he or she may, at any time, contact any employee of the controller.</p>
        </li>
        <li><h4 className="landing-h4">c) Right to rectification </h4>
        <p>Each data subject shall have the right granted by the European legislator to obtain from the controller without undue delay the rectification of inaccurate personal data concerning him or her. Taking into account the purposes of the processing, the data subject shall have the right to have incomplete personal data completed, including by means of providing a supplementary statement.</p>
        
        <p>If a data subject wishes to exercise this right to rectification, he or she may, at any time, contact any employee of the controller.</p></li>
        <li>
        <h4 className="landing-h4">d) Right to erasure (Right to be forgotten) </h4>
        <p>Each data subject shall have the right granted by the European legislator to obtain from the controller the erasure of personal data concerning him or her without undue delay, and the controller shall have the obligation to erase personal data without undue delay where one of the following grounds applies, as long as the processing is not necessary: </p>
        
        <ul className="no-list">
        <li>The personal data are no longer necessary in relation to the purposes for which they were collected or otherwise processed.</li>
        <li>The data subject withdraws consent to which the processing is based according to point (a) of Article 6(1) of the GDPR, or point (a) of Article 9(2) of the GDPR, and where there is no other legal ground for the processing.</li>
        <li>The data subject objects to the processing pursuant to Article 21(1) of the GDPR and there are no overriding legitimate grounds for the processing, or the data subject objects to the processing pursuant to Article 21(2) of the GDPR. </li>
        <li>The personal data have been unlawfully processed.</li>
        <li>The personal data must be erased for compliance with a legal obligation in Union or Member State law to which the controller is subject.</li>
        <li>The personal data have been collected in relation to the offer of information society services referred to in Article 8(1) of the GDPR.</li>
        
        </ul>
        <p>If one of the aforementioned reasons applies, and a data subject wishes to request the erasure of personal data stored by the a, he or she may, at any time, contact any employee of the controller. An employee of a shall promptly ensure that the erasure request is complied with immediately.</p>
        
        <p>Where the controller has made personal data public and is obliged pursuant to Article 17(1) to erase the personal data, the controller, taking account of available technology and the cost of implementation, shall take reasonable steps, including technical measures, to inform other controllers processing the personal data that the data subject has requested erasure by such controllers of any links to, or copy or replication of, those personal data, as far as processing is not required. An employees of the a will arrange the necessary measures in individual cases.</p>
        </li>
        <li><h4 className="landing-h4">e) Right of restriction of processing</h4>
        <p>Each data subject shall have the right granted by the European legislator to obtain from the controller restriction of processing where one of the following applies:</p>
        
        <ul className="no-list">
        <li>The accuracy of the personal data is contested by the data subject, for a period enabling the controller to verify the accuracy of the personal data. </li>
        <li>The processing is unlawful and the data subject opposes the erasure of the personal data and requests instead the restriction of their use instead.</li>
        <li>The controller no longer needs the personal data for the purposes of the processing, but they are required by the data subject for the establishment, exercise or defence of legal claims.</li>
        <li>The data subject has objected to processing pursuant to Article 21(1) of the GDPR pending the verification whether the legitimate grounds of the controller override those of the data subject.</li>
        
        </ul>
        <p>If one of the aforementioned conditions is met, and a data subject wishes to request the restriction of the processing of personal data stored by the a, he or she may at any time contact any employee of the controller. The employee of the a will arrange the restriction of the processing. </p>
        </li>
        <li><h4 className="landing-h4">f) Right to data portability</h4>
        <p>Each data subject shall have the right granted by the European legislator, to receive the personal data concerning him or her, which was provided to a controller, in a structured, commonly used and machine-readable format. He or she shall have the right to transmit those data to another controller without hindrance from the controller to which the personal data have been provided, as long as the processing is based on consent pursuant to point (a) of Article 6(1) of the GDPR or point (a) of Article 9(2) of the GDPR, or on a contract pursuant to point (b) of Article 6(1) of the GDPR, and the processing is carried out by automated means, as long as the processing is not necessary for the performance of a task carried out in the public interest or in the exercise of official authority vested in the controller.</p>
        
        <p>Furthermore, in exercising his or her right to data portability pursuant to Article 20(1) of the GDPR, the data subject shall have the right to have personal data transmitted directly from one controller to another, where technically feasible and when doing so does not adversely affect the rights and freedoms of others.</p>
        
        <p>In order to assert the right to data portability, the data subject may at any time contact any employee of the a.</p>
        
        </li>
        <li>
        <h4 className="landing-h4">g) Right to object</h4>
        <p>Each data subject shall have the right granted by the European legislator to object, on grounds relating to his or her particular situation, at any time, to processing of personal data concerning him or her, which is based on point (e) or (f) of Article 6(1) of the GDPR. This also applies to profiling based on these provisions.</p>
        
        <p>The a shall no longer process the personal data in the event of the objection, unless we can demonstrate compelling legitimate grounds for the processing which override the interests, rights and freedoms of the data subject, or for the establishment, exercise or defence of legal claims.</p>
        
        <p>If the a processes personal data for direct marketing purposes, the data subject shall have the right to object at any time to processing of personal data concerning him or her for such marketing. This applies to profiling to the extent that it is related to such direct marketing. If the data subject objects to the a to the processing for direct marketing purposes, the a will no longer process the personal data for these purposes.</p>
        
        <p>In addition, the data subject has the right, on grounds relating to his or her particular situation, to object to processing of personal data concerning him or her by the a for scientific or historical research purposes, or for statistical purposes pursuant to Article 89(1) of the GDPR, unless the processing is necessary for the performance of a task carried out for reasons of public interest.</p>
        
        <p>In order to exercise the right to object, the data subject may contact any employee of the a. In addition, the data subject is free in the context of the use of information society services, and notwithstanding Directive 2002/58/EC, to use his or her right to object by automated means using technical specifications.</p>
        </li>
        <li><h4 className="landing-h4">h) Automated individual decision-making, including profiling</h4>
        <p>Each data subject shall have the right granted by the European legislator not to be subject to a decision based solely on automated processing, including profiling, which produces legal effects concerning him or her, or similarly significantly affects him or her, as long as the decision (1) is not is necessary for entering into, or the performance of, a contract between the data subject and a data controller, or (2) is not authorised by Union or Member State law to which the controller is subject and which also lays down suitable measures to safeguard the data subject's rights and freedoms and legitimate interests, or (3) is not based on the data subject's explicit consent.</p>
        
        <p>If the decision (1) is necessary for entering into, or the performance of, a contract between the data subject and a data controller, or (2) it is based on the data subject's explicit consent, the a shall implement suitable measures to safeguard the data subject's rights and freedoms and legitimate interests, at least the right to obtain human intervention on the part of the controller, to express his or her point of view and contest the decision.</p>
        
        <p>If the data subject wishes to exercise the rights concerning automated individual decision-making, he or she may, at any time, contact any employee of the a.</p>
        
        </li>
        <li><h4 className="landing-h4">i) Right to withdraw data protection consent </h4>
        <p>Each data subject shall have the right granted by the European legislator to withdraw his or her consent to processing of his or her personal data at any time. </p>
        
        <p>If the data subject wishes to exercise the right to withdraw the consent, he or she may, at any time, contact any employee of the a.</p>
        
        </li>
        </ul>
        <h3 className="landing-h3">11. Data protection provisions about the application and use of Facebook</h3>
        <p>On this website, the controller has integrated components of the enterprise Facebook. Facebook is a social network.</p>
        
        <p>A social network is a place for social meetings on the Internet, an online community, which usually allows users to communicate with each other and interact in a virtual space. A social network may serve as a platform for the exchange of opinions and experiences, or enable the Internet community to provide personal or business-related information. Facebook allows social network users to include the creation of private profiles, upload photos, and network through friend requests.</p>
        
        <p>The operating company of Facebook is Facebook, Inc., 1 Hacker Way, Menlo Park, CA 94025, United States. If a person lives outside of the United States or Canada, the controller is the Facebook Ireland Ltd., 4 Grand Canal Square, Grand Canal Harbour, Dublin 2, Ireland.</p>
        
        <p>With each call-up to one of the individual pages of this Internet website, which is operated by the controller and into which a Facebook component (Facebook plug-ins) was integrated, the web browser on the information technology system of the data subject is automatically prompted to download display of the corresponding Facebook component from Facebook through the Facebook component. An overview of all the Facebook Plug-ins may be accessed under https://developers.facebook.com/docs/plugins/. During the course of this technical procedure, Facebook is made aware of what specific sub-site of our website was visited by the data subject.</p>
        
        <p>If the data subject is logged in at the same time on Facebook, Facebook detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-site of our Internet page was visited by the data subject. This information is collected through the Facebook component and associated with the respective Facebook account of the data subject. If the data subject clicks on one of the Facebook buttons integrated into our website, e.g. the "Like" button, or if the data subject submits a comment, then Facebook matches this information with the personal Facebook user account of the data subject and stores the personal data.</p>
        
        <p>Facebook always receives, through the Facebook component, information about a visit to our website by the data subject, whenever the data subject is logged in at the same time on Facebook during the time of the call-up to our website. This occurs regardless of whether the data subject clicks on the Facebook component or not. If such a transmission of information to Facebook is not desirable for the data subject, then he or she may prevent this by logging off from their Facebook account before a call-up to our website is made.</p>
        
        <p>The data protection guideline published by Facebook, which is available at https://facebook.com/about/privacy/, provides information about the collection, processing and use of personal data by Facebook. In addition, it is explained there what setting options Facebook offers to protect the privacy of the data subject. In addition, different configuration options are made available to allow the elimination of data transmission to Facebook. These applications may be used by the data subject to eliminate a data transmission to Facebook.</p>
        
        <h3 className="landing-h3">12. Data protection provisions about the application and use of Google+</h3>
        <p>On this website, the controller has integrated the Google+ button as a component. Google+ is a so-called social network. A social network is a social meeting place on the Internet, an online community, which usually allows users to communicate with each other and interact in a virtual space. A social network may serve as a platform for the exchange of opinions and experiences, or enable the Internet community to provide personal or business-related information. Google+ allows users of the social network to include the creation of private profiles, upload photos and network through friend requests.</p>
        
        <p>The operating company of Google+ is Google Ireland Limited, Gordon House, Barrow Street, Dublin, D04 E5W5, Ireland.</p>
        
        <p>With each call-up to one of the individual pages of this website, which is operated by the controller and on which a Google+ button has been integrated, the Internet browser on the information technology system of the data subject automatically downloads a display of the corresponding Google+ button of Google through the respective Google+ button component. During the course of this technical procedure, Google is made aware of what specific sub-page of our website was visited by the data subject. More detailed information about Google+ is available under https://developers.google.com/+/.</p>
        
        <p>If the data subject is logged in at the same time to Google+, Google recognizes with each call-up to our website by the data subject and for the entire duration of his or her stay on our Internet site, which specific sub-pages of our Internet page were visited by the data subject. This information is collected through the Google+ button and Google matches this with the respective Google+ account associated with the data subject.</p>
        
        <p>If the data subject clicks on the Google+ button integrated on our website and thus gives a Google+ 1 recommendation, then Google assigns this information to the personal Google+ user account of the data subject and stores the personal data. Google stores the Google+ 1 recommendation of the data subject, making it publicly available in accordance with the terms and conditions accepted by the data subject in this regard. Subsequently, a Google+ 1 recommendation given by the data subject on this website together with other personal data, such as the Google+ account name used by the data subject and the stored photo, is stored and processed on other Google services, such as search-engine results of the Google search engine, the Google account of the data subject or in other places, e.g. on Internet pages, or in relation to advertisements. Google is also able to link the visit to this website with other personal data stored on Google. Google further records this personal information with the purpose of improving or optimizing the various Google services.</p>
        
        <p>Through the Google+ button, Google receives information that the data subject visited our website, if the data subject at the time of the call-up to our website is logged in to Google+. This occurs regardless of whether the data subject clicks or doesn’t click on the Google+ button.</p>
        
        <p>If the data subject does not wish to transmit personal data to Google, he or she may prevent such transmission by logging out of his Google+ account before calling up our website.</p>
        
        <p>Further information and the data protection provisions of Google may be retrieved under https://www.google.com/intl/en/policies/privacy/. More references from Google about the Google+ 1 button may be obtained under https://developers.google.com/+/web/buttons-policy.</p>
        
        <h3 className="landing-h3">13. Data protection provisions about the application and use of Instagram</h3>
        <p>On this website, the controller has integrated components of the service Instagram. Instagram is a service that may be qualified as an audiovisual platform, which allows users to share photos and videos, as well as disseminate such data in other social networks.</p>
        
        <p>The operating company of the services offered by Instagram is Facebook Ireland Ltd., 4 Grand Canal Square, Grand Canal Harbour, Dublin 2 Ireland.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which an Instagram component (Insta button) was integrated, the Internet browser on the information technology system of the data subject is automatically prompted to the download of a display of the corresponding Instagram component of Instagram. During the course of this technical procedure, Instagram becomes aware of what specific sub-page of our website was visited by the data subject.</p>
        
        <p>If the data subject is logged in at the same time on Instagram, Instagram detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-page of our Internet page was visited by the data subject. This information is collected through the Instagram component and is associated with the respective Instagram account of the data subject. If the data subject clicks on one of the Instagram buttons integrated on our website, then Instagram matches this information with the personal Instagram user account of the data subject and stores the personal data.</p>
        
        <p>Instagram receives information via the Instagram component that the data subject has visited our website provided that the data subject is logged in at Instagram at the time of the call to our website. This occurs regardless of whether the person clicks on the Instagram button or not. If such a transmission of information to Instagram is not desirable for the data subject, then he or she can prevent this by logging off from their Instagram account before a call-up to our website is made.</p>
        
        <p>Further information and the applicable data protection provisions of Instagram may be retrieved under https://help.instagram.com/155833707900388 and https://www.instagram.com/about/legal/privacy/.</p>
        
        <h3 className="landing-h3">14. Data protection provisions about the application and use of LinkedIn</h3>
        <p>The controller has integrated components of the LinkedIn Corporation on this website. LinkedIn is a web-based social network that enables users with existing business contacts to connect and to make new business contacts. Over 400 million registered people in more than 200 countries use LinkedIn. Thus, LinkedIn is currently the largest platform for business contacts and one of the most visited websites in the world.</p>
        
        <p>The operating company of LinkedIn is LinkedIn Corporation, 2029 Stierlin Court Mountain View, CA 94043, UNITED STATES. For privacy matters outside of the UNITED STATES LinkedIn Ireland, Privacy Policy Issues, Wilton Plaza, Wilton Place, Dublin 2, Ireland, is responsible.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which a LinkedIn component (LinkedIn plug-in) was integrated, the Internet browser on the information technology system of the data subject is automatically prompted to the download of a display of the corresponding LinkedIn component of LinkedIn. Further information about the LinkedIn plug-in may be accessed under https://developer.linkedin.com/plugins. During the course of this technical procedure, LinkedIn gains knowledge of what specific sub-page of our website was visited by the data subject.</p>
        
        <p>If the data subject is logged in at the same time on LinkedIn, LinkedIn detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-page of our Internet page was visited by the data subject. This information is collected through the LinkedIn component and associated with the respective LinkedIn account of the data subject. If the data subject clicks on one of the LinkedIn buttons integrated on our website, then LinkedIn assigns this information to the personal LinkedIn user account of the data subject and stores the personal data.</p>
        
        <p>LinkedIn receives information via the LinkedIn component that the data subject has visited our website, provided that the data subject is logged in at LinkedIn at the time of the call-up to our website. This occurs regardless of whether the person clicks on the LinkedIn button or not. If such a transmission of information to LinkedIn is not desirable for the data subject, then he or she may prevent this by logging off from their LinkedIn account before a call-up to our website is made.</p>
        
        <p>LinkedIn provides under https://www.linkedin.com/psettings/guest-controls the possibility to unsubscribe from e-mail messages, SMS messages and targeted ads, as well as the ability to manage ad settings. LinkedIn also uses affiliates such as Eire, Google Analytics, BlueKai, DoubleClick, Nielsen, Comscore, Eloqua, and Lotame. The setting of such cookies may be denied under https://www.linkedin.com/legal/cookie-policy. The applicable privacy policy for LinkedIn is available under https://www.linkedin.com/legal/privacy-policy. The LinkedIn Cookie Policy is available under https://www.linkedin.com/legal/cookie-policy.</p>
        
        <h3 className="landing-h3">15. Data protection provisions about the application and use of Myspace</h3>
        
        <p>On this website, the controller has integrated components of MySpace LLC. MySpace is a so-called social network. A social network is an Internet social meeting place, an online community that allows users to communicate and interact with each other in a virtual space. A social network can serve as a platform for the exchange of opinions and experiences or allow the Internet community to provide personal or company-related information. MySpace allows users of the social network to create free blogs or groups of users, including photos and videos.</p>
        
        <p>The operating company of MySpace is Myspace LLC, 6100 Center Drive, Suite 800, 90045 Los Angeles, USA.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which a MySpace component (MySpace plug-in) was integrated, the Internet browser on the information technology system of the data subject is automatically prompted to the download through the respective MySpace component a display of the corresponding MySpace component of MySpace. Further information about MySpace is available under https://myspace.com. During the course of this technical procedure, MySpace gains knowledge of what specific sub-page of our website is visited by the data subject.</p>
        
        <p>If the data subject is logged in at the same time on MySpace, MySpace detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-page of our Internet page was visited by the data subject. This information is collected through the MySpace component and associated with the respective MySpace account of the data subject. If the data subject clicks on one of the MySpace buttons, integrated on our website, then MySpace assigns this information with the personal MySpace user account of the data subject and stores the personal data.</p>
        
        <p>MySpace receives information via the MySpace component that the data subject has visited our website, provided that the data subject is logged in at MySpace at the time of the call to our website. This occurs regardless of whether the person clicks on the MySpace component or not. If such a transmission of information to MySpace is not desirable for the data subject, then he or she may prevent this by logging off from their MySpace account before a call-up to our website is made.</p>
        
        <p>The data protection guideline published by MySpace, which is available under https://myspace.com/pages/privacy, provides information on the collection, processing and use of personal data by MySpace.</p>
        
        <h3 className="landing-h3">16. Data protection provisions about the application and use of Pinterest</h3>
        <p>On this website, the controller has integrated components of Pinterest Inc. Pinterest is a so-called social network. A social network is an Internet social meeting place, an online community that allows users to communicate and interact with each other in a virtual space. A social network may serve as a platform for the exchange of opinions and experiences, or allow the Internet community to provide personal or company-related information. Pinterest enables the users of the social network to publish, inter alia, picture collections and individual pictures as well as descriptions on virtual pinboards (so-called pins), which can then be shared by other user's (so-called re-pins) or commented on.</p>
        
        <p>The operating company of Pinterest is Pinterest Europe Ltd., Palmerston House, 2nd Floor, Fenian Street, Dublin 2,Ireland.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which a Pinterest component (Pinterest plug-in) was integrated, the Internet browser on the information technology system of the data subject automatically prompted to download through the respective Pinterest component a display of the corresponding Pinterest component. Further information on Pinterest is available under https://pinterest.com/. During the course of this technical procedure, Pinterest gains knowledge of what specific sub-page of our website is visited by the data subject.</p>
        
        <p>If the data subject is logged in at the same time on Pinterest, Pinterest detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-page of our Internet page was visited by the data subject. This information is collected through the Pinterest component and associated with the respective Pinterest account of the data subject. If the data subject clicks on one of the Pinterest buttons, integrated on our website, then Pinterest assigns this information to the personal Pinterest user account of the data subject and stores the personal data.</p>
        
        <p>Pinterest receives information via the Pinterest component that the data subject has visited our website, provided that the data subject is logged in at Pinterest at the time of the call-up to our website. This occurs regardless of whether the person clicks on the Pinterest component or not. If such a transmission of information to Pinterest is not desirable for the data subject, then he or she may prevent this by logging off from their Pinterest account before a call-up to our website is made.</p>
        
        <p>The data protection guideline published by Pinterest, which is available under https://about.pinterest.com/privacy-policy, provides information on the collection, processing and use of personal data by Pinterest.</p>
        
        <h3 className="landing-h3">17. Data protection provisions about the application and use of Tumblr</h3>
        <p>On this website, the controller has integrated components of Tumblr. Tumblr is a platform that allows users to create and run a blog. A blog is a web-based, generally publicly-accessible portal on which one or more people called bloggers or web bloggers may post articles or write down thoughts in so-called blogposts. For example, in a Tumblr blog the user can publish text, images, links, and videos, and spread them in the digital space. Furthermore, Tumblr users may import content from other websites into their own blog.</p>
        
        <p>The operating company of Tumblr is Oath (EMEA) Limited, 5-7 Point Square, North Wall Quay, Dublin 1, Ireland.</p>
        
        <p>Through each call to one of the individual pages of this Internet site, which is operated by the controller and on which a Tumblr component (Tumblr button) has been integrated, the Internet browser on the information technology system of the data subject causes automatically the download of a display of the corresponding Tumblr component of Tumblr. Learn more about the Tumblr-buttons that are available under https://www.tumblr.com/buttons. During the course of this technical procedure, Tumblr becomes aware of what concrete sub-page of our website was visited by the data subject. The purpose of the integration of the Tumblr component is a retransmission of the contents of this website to allow our users to introduce this web page to the digital world and to increase our visitor numbers.</p>
        
        <p>If the data subject is logged in at Tumblr, Tumblr detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-page of our Internet page was visited by the data subject. This information is collected through the Tumblr component and associated with the respective Tumblr account of the data subject. If the data subject clicks on one of the Tumblr buttons, integrated on our website, then Tumblr assigns this information to the personal Tumblr user account of the data subject and stores the personal data.</p>
        
        <p>Tumblr receives information via the Tumblr component that the data subject has visited our website, provided that the data subject is logged in at Tumblr at the time of the call-up to our website. This occurs regardless of whether the person clicks on the Tumblr component or not. If such a transfer of information to Tumblr is not desirable for the data subject, then he or she may prevent this by logging off from their Tumblr account before a call-up to our website is made.</p>
        
        <p>The applicable data protection provisions of Tumblr may be accessed under https://www.tumblr.com/policy/en/privacy.</p>
        
        <h3 className="landing-h3">18. Data protection provisions about the application and use of Twitter</h3>
        <p>On this website, the controller has integrated components of Twitter. Twitter is a multilingual, publicly-accessible microblogging service on which users may publish and spread so-called ‘tweets,’ e.g. short messages, which are limited to 280 characters. These short messages are available for everyone, including those who are not logged on to Twitter. The tweets are also displayed to so-called followers of the respective user. Followers are other Twitter users who follow a user's tweets. Furthermore, Twitter allows you to address a wide audience via hashtags, links or retweets.</p>
        
        <p>The operating company of Twitter is Twitter International Company, One Cumberland Place, Fenian Street Dublin 2, D02 AX07, Ireland.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which a Twitter component (Twitter button) was integrated, the Internet browser on the information technology system of the data subject is automatically prompted to download a display of the corresponding Twitter component of Twitter. Further information about the Twitter buttons is available under https://about.twitter.com/de/resources/buttons. During the course of this technical procedure, Twitter gains knowledge of what specific sub-page of our website was visited by the data subject. The purpose of the integration of the Twitter component is a retransmission of the contents of this website to allow our users to introduce this web page to the digital world and increase our visitor numbers.</p>
        
        <p>If the data subject is logged in at the same time on Twitter, Twitter detects with every call-up to our website by the data subject and for the entire duration of their stay on our Internet site which specific sub-page of our Internet page was visited by the data subject. This information is collected through the Twitter component and associated with the respective Twitter account of the data subject. If the data subject clicks on one of the Twitter buttons integrated on our website, then Twitter assigns this information to the personal Twitter user account of the data subject and stores the personal data.</p>
        
        <p>Twitter receives information via the Twitter component that the data subject has visited our website, provided that the data subject is logged in on Twitter at the time of the call-up to our website. This occurs regardless of whether the person clicks on the Twitter component or not. If such a transmission of information to Twitter is not desirable for the data subject, then he or she may prevent this by logging off from their Twitter account before a call-up to our website is made.</p>
        
        <p>The applicable data protection provisions of Twitter may be accessed under https://twitter.com/privacy?lang=en.</p>
        
        <h3 className="landing-h3">19. Data protection provisions about the application and use of Xing</h3>
        <p>On this website, the controller has integrated components of XING. XING is an Internet-based social network that enables users to connect with existing business contacts and to create new business contacts. The individual users can create a personal profile of themselves at XING. Companies may, e.g. create company profiles or publish jobs on XING.</p>
        
        <p>The operating company of XING is XING SE, Dammtorstraße 30, 20354 Hamburg, Germany.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which a XING component (XING plug-in) was integrated, the Internet browser on the information technology system of the data subject is automatically prompted to download a display of the corresponding XING component of XING. Further information about the XING plug-in the may be accessed under https://dev.xing.com/plugins. During the course of this technical procedure, XING gains knowledge of what specific sub-page of our website was visited by the data subject.</p>
        
        <p>If the data subject is logged in at the same time on XING, XING detects with every call-up to our website by the data subject—and for the entire duration of their stay on our Internet site—which specific sub-page of our Internet page was visited by the data subject. This information is collected through the XING component and associated with the respective XING account of the data subject. If the data subject clicks on the XING button integrated on our Internet site, e.g. the "Share"-button, then XING assigns this information to the personal XING user account of the data subject and stores the personal data.</p>
        
        <p>XING receives information via the XING component that the data subject has visited our website, provided that the data subject is logged in at XING at the time of the call to our website. This occurs regardless of whether the person clicks on the XING component or not. If such a transmission of information to XING is not desirable for the data subject, then he or she can prevent this by logging off from their XING account before a call-up to our website is made.</p>
        
        <p>The data protection provisions published by XING, which is available under https://www.xing.com/privacy, provide information on the collection, processing and use of personal data by XING. In addition, XING has published privacy notices for the XING share button under https://www.xing.com/app/share?op=data_protection.</p>
        
        <h3 className="landing-h3">20. Data protection provisions about the application and use of YouTube</h3>
        <p>On this website, the controller has integrated components of YouTube. YouTube is an Internet video portal that enables video publishers to set video clips and other users free of charge, which also provides free viewing, review and commenting on them. YouTube allows you to publish all kinds of videos, so you can access both full movies and TV broadcasts, as well as music videos, trailers, and videos made by users via the Internet portal.
        </p>
        
        <p>The operating company of YouTube is Google Ireland Limited, Gordon House, Barrow Street, Dublin, D04 E5W5, Ireland.</p>
        
        <p>With each call-up to one of the individual pages of this Internet site, which is operated by the controller and on which a YouTube component (YouTube video) was integrated, the Internet browser on the information technology system of the data subject is automatically prompted to download a display of the corresponding YouTube component. Further information about YouTube may be obtained under https://www.youtube.com/yt/about/en/. During the course of this technical procedure, YouTube and Google gain knowledge of what specific sub-page of our website was visited by the data subject.</p>
        
        <p>If the data subject is logged in on YouTube, YouTube recognizes with each call-up to a sub-page that contains a YouTube video, which specific sub-page of our Internet site was visited by the data subject. This information is collected by YouTube and Google and assigned to the respective YouTube account of the data subject.</p>
        
        <p>YouTube and Google will receive information through the YouTube component that the data subject has visited our website, if the data subject at the time of the call to our website is logged in on YouTube; this occurs regardless of whether the person clicks on a YouTube video or not. If such a transmission of this information to YouTube and Google is not desirable for the data subject, the delivery may be prevented if the data subject logs off from their own YouTube account before a call-up to our website is made.</p>
        
        <p>YouTube's data protection provisions, available at https://www.google.com/intl/en/policies/privacy/, provide information about the collection, processing and use of personal data by YouTube and Google.</p>
        
        <h3 className="landing-h3">21. Legal basis for the processing </h3>
        <p>Art. 6(1) lit. a GDPR serves as the legal basis for processing operations for which we obtain consent for a specific processing purpose. If the processing of personal data is necessary for the performance of a contract to which the data subject is party, as is the case, for example, when processing operations are necessary for the supply of goods or to provide any other service, the processing is based on Article 6(1) lit. b GDPR. The same applies to such processing operations which are necessary for carrying out pre-contractual measures, for example in the case of inquiries concerning our products or services. Is our company subject to a legal obligation by which processing of personal data is required, such as for the fulfillment of tax obligations, the processing is based on Art. 6(1) lit. c GDPR.
        In rare cases, the processing of personal data may be necessary to protect the vital interests of the data subject or of another natural person. This would be the case, for example, if a visitor were injured in our company and his name, age, health insurance data or other vital information would have to be passed on to a doctor, hospital or other third party. Then the processing would be based on Art. 6(1) lit. d GDPR.
        Finally, processing operations could be based on Article 6(1) lit. f GDPR. This legal basis is used for processing operations which are not covered by any of the abovementioned legal grounds, if processing is necessary for the purposes of the legitimate interests pursued by our company or by a third party, except where such interests are overridden by the interests or fundamental rights and freedoms of the data subject which require protection of personal data. Such processing operations are particularly permissible because they have been specifically mentioned by the European legislator. He considered that a legitimate interest could be assumed if the data subject is a client of the controller (Recital 47 Sentence 2 GDPR).
        </p>
        
        <h3 className="landing-h3">22. The legitimate interests pursued by the controller or by a third party</h3>
        <p>Where the processing of personal data is based on Article 6(1) lit. f GDPR our legitimate interest is to carry out our business in favor of the well-being of all our employees and the shareholders.</p>
        
        <h3 className="landing-h3">23. Period for which the personal data will be stored</h3>
        <p>The criteria used to determine the period of storage of personal data is the respective statutory retention period. After expiration of that period, the corresponding data is routinely deleted, as long as it is no longer necessary for the fulfillment of the contract or the initiation of a contract.</p>
        
        <h3 className="landing-h3">24. Provision of personal data as statutory or contractual requirement; Requirement necessary to enter into a contract; Obligation of the data subject to provide the personal data; possible consequences of failure to provide such data </h3>
        <p>We clarify that the provision of personal data is partly required by law (e.g. tax regulations) or can also result from contractual provisions (e.g. information on the contractual partner).
        
        Sometimes it may be necessary to conclude a contract that the data subject provides us with personal data, which must subsequently be processed by us. The data subject is, for example, obliged to provide us with personal data when our company signs a contract with him or her. The non-provision of the personal data would have the consequence that the contract with the data subject could not be concluded.
        
        Before personal data is provided by the data subject, the data subject must contact any employee. The employee clarifies to the data subject whether the provision of the personal data is required by law or contract or is necessary for the conclusion of the contract, whether there is an obligation to provide the personal data and the consequences of non-provision of the personal data.
        </p>
        
        <h3 className="landing-h3">25. Existence of automated decision-making</h3>
        <p>As a responsible company, we do not use automatic decision-making or profiling.</p>
        
        <p>This Privacy Policy has been generated by the Privacy Policy Generator of the <a href="https://dg-datenschutz.de/?lang=en">German Association for Data Protection</a> that was developed in cooperation with  <a href="https://www.wbs-law.de/eng/practice-areas/internet-law/it-law/">Privacy Lawyers</a> from WILDE BEUGER SOLMECKE, Cologne.
        </p>
        </div>
        <Footer/>
        </div>
        </div>) ;        
    }
}