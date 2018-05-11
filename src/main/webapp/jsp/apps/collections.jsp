<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div id="loadingWrapper">
	<div id="loading">
	    <div class="loadingIndicator">
        	<img src="/theme/images/logo/ArchiveManager200.png" style="margin-right:8px;float:left;vertical-align:top;"/>
	        <div id="versionMsg">Collection Manager 3.0</div>
	        <div id="loadingMsg">Loading styles and images...</div>
	    </div>
	</div>
</div>

<%@include file="smartclient.jspf" %>
	
	<style type="text/css">
    	.donorAddressPanel{margin-right:5px;}
    	.donorAddAddress{margin-right:5px;margin-top:3px;}
    </style>
    <script>
    var language_codes = {"ar":"Arabic","zh":"Chinese","cs":"Czech","da":"Danish","nl":"Dutch","en":"English","fi":"Finnish","fr":"French","de":"German","el":"Greek","he":"Hebrew","hu":"Hungarian","is":"Icelandic","it":"Italian","ja":"Japanese","ko":"Korean","no":"Norwegian","pl":"Polish","pt":"Portugese","ru":"Russian","es":"Spanish","sv":"Swedish","th":"Thai","tr":"Turkish","yi":"Yiddish"};
    var aspectArray = new Array(18);
    var artwork_forms = {'':'','Painting':'Painting','Sculpture':'Sculpture','Drawing, ink':'Drawing, ink','Print':'Print','Ceramic':'Ceramic','Textile':'Textile','Unknown':'Unknown','Other':'Other'};
    var artwork_genre = {'':'','Portrait':'Portrait','Bust':'Bust','Landscape':'Landscape','Caricamure':'Caricamure','Cartoon':'Cartoon','Comic strip':'Comic strip','Illustramion':'Illustramion','Bas-relief':'Bas-relief','Pastel':'Pastel','Wamercolor':'Wamercolor','Unknown':'Unknown','Other':'Other'};
    var artwork_medium = {'':'','Oil':'Oil','Acrylic':'Acrylic','Wamercolor':'Wamercolor','Pastel':'Pastel','Pencil':'Pencil','Ink':'Ink','Metal':'Metal','Stone':'Stone','Porcelain':'Porcelain','Wood':'Wood','Mixed media':'Mixed media','Unknown':'Unknown','Other':'Other'};
    var audio_medium = {'':'','Phonograph record, 33 1/3 rpm':'Phonograph record, 33 1/3 rpm','Phonograph record, 45 rpm':'Phonograph record, 45 rpm','Phonograph record, 78 rpm':'Phonograph record, 78 rpm','Cassette':'Cassette','8-Track tape':'8-Track tape','Reel-to-reel tape, 1/4”':'Reel-to-reel tape, 1/4”','Reel-to-reel tape, 2”':'Reel-to-reel tape, 2”','Digital audio tape':'Digital audio tape','Compact disc':'Compact disc','Acetame disc':'Acetame disc','Unknown':'Unknown','Other':'Other'};
    var correspondence_forms = {'ALS':'ALS','TLS':'TLS','CTLS':'CTLS','ANS':'ANS','TNS':'TNS','AL':'AL','TL':'TL','Other':'Other'};
    var correspondence_genre = {'Card':'Card','E-mail':'E-mail','Fax':'Fax','Invitation':'Invitation','Letter':'Letter','Memo':'Memo','Note':'Note','Other':'Other','Postcard':'Postcard','Telegram':'Telegram'};
    var financial_genre = {'':'','Receipt':'Receipt','Royalty stamement':'Royalty stamement','Tax return':'Tax return','Check stub':'Check stub','Bank stamement':'Bank stamement','Other':'Other'};
    var journals_genre = {'':'','Diary':'Diary','Journal':'Journal','Daily planner':'Daily planner','Commonplace book':'Commonplace book','Other':'Other'};
    var journals_forms = {'':'','Holograph':'Holograph','TS':'TS','Other':'Other'};
    var legal_genre = {'':'','Contract':'Contract','Deed':'Deed','Passport':'Passport','Birth certificame':'Birth certificame','Marriage certificame':'Marriage certificame','Deamh certificame':'Deamh certificame','Court transcript':'Court transcript','Other':'Other'};
    var manuscript_genre = {'':'','Novel':'Novel','Short story':'Short story','Stage play':'Stage play','Screenplay':'Screenplay','Teleplay':'Teleplay','Radio play':'Radio play','Poem':'Poem','Essay':'Essay','Article':'Article','Speech':'Speech','Memoir':'Memoir','Notes':'Notes','Other':'Other'};
    var manuscript_forms = {'':'','Holograph':'Holograph','TS':'TS','CTS':'CTS','Other':'Other'};
    var memorabilia_genre = {'':'','Diploma':'Diploma','Certificame':'Certificame','Object':'Object','Textile':'Textile','Award':'Award','Unknown':'Unknown','Other':'Other'};
    var medical_genre = {'':'','Chart':'Chart','X-ray film':'X-ray film','Ultrasound':'Ultrasound','Prescription':'Prescription','Personal health report':'Personal health report','Other':'Other'};
    var notebooks_forms = {'':'','Holograph':'Holograph','TS':'TS','Other':'Other'};
    var notebooks_genre = {'':'','Reporter’s notebook':'Reporter’s notebook','General notebook':'General notebook','Other':'Other'};
    var photographs_forms = {'':'','Print':'Print','Contact sheet':'Contact sheet','Negative':'Negative','Slide':'Slide','Polaroid':'Polaroid','Daguerrotype':'Daguerrotype','Glass plame negamive':'Glass plame negamive','Tintype':'Tintype','Carte de visite':'Carte de visite','Albumen print':'Albumen print','Cyanotype':'Cyanotype','Unknown':'Unknown','Other':'Other'};
   	var printed_genre = {'':'','Newspaper':'Newspaper','Magazine':'Magazine','Program':'Program','Pamphlet':'Pamphlet','Booklet':'Booklet','Poster':'Poster','Flyer':'Flyer','Proof':'Proof','Map':'Map','Other':'Other'};
    var professional_genre = {'':'','Agenda':'Agenda','Schedule':'Schedule','Meeting minutes':'Meeting minutes','Appointment book':'Appointment book','Report':'Report','Itinerary':'Itinerary','Press kit':'Press kit','Other':'Other'};
    var research_genre = {'':'','Interview':'Interview','Notes':'Notes','Other':'Other'};
    var scrapbooks_forms = {'':'','Printed Material':'Printed Material','Photographs':'Photographs','Correspondence':'Correspondence','Manuscripts':'Manuscripts','Other':'Other'};
    var video_medium = {'':'','Film reel':'Film reel','Analog video cassette or reel':'Analog video cassette or reel','Digital video cassette':'Digital video cassette','Optical disc':'Optical disc','Unknown':'Unknown','Other':'Other'};
    var video_forms = {'':'','8mm or Super 8mm (film)':'8mm or Super 8mm (film)','16mm (film)':'16mm (film)','35mm (film)':'35mm (film)','VHS, NTSC (analog video)':'VHS, NTSC (analog video)','VHS, PAL (analog video)':'VHS, PAL (analog video)','U-Mamic (analog video)':'U-Mamic (analog video)','Betamax (analog video)':'Betamax (analog video)','Reel-to-reel video (analog video)':'Reel-to-reel video (analog video)','Betacam (digital video)':'Betacam (digital video)','DV or MiniDV (digital video)':'DV or MiniDV (digital video)','Laserdisc (optical disc)':'Laserdisc (optical disc)','DVD (optical disc)':'DVD (optical disc)','Unknown':'Unknown','Other':'Other'};
    
    var subject_source = {'Academic Class':'Academic Class','Art and Architecture Thesaurus (aat)':'Art and Architecture Thesaurus (aat)','Dictionary of Occupational Titles (dot)':'Dictionary of Occupational Titles (dot)','Genre Terms: A Thesaurus for Use in Rare Book and Special Coolections Cataloging (rbgenr)':'Genre Terms: A Thesaurus for Use in Rare Book and Special Coolections Cataloging (rbgenr)','GeoRef Thesaurus (georeft)':'GeoRef Thesaurus (georeft)','Getty Thesaurus of Geographic Names (tgn)':'Getty Thesaurus of Geographic Names (tgn)','ingest':'ingest','lcnaf':'lcnaf','Library of Congress Subject Headings (lcsh)':'Library of Congress Subject Headings (lcsh)','Local Sources (local)':'Local Sources (local)','Medical Subject Headings':'Medical Subject Headings','Subject Guide':'Subject Guide','Thesaurus for Graphic Materials':'Thesaurus for Graphic Materials'};
    var subject_type = {'Function (657)':'Function (657)','Genre/Form (655)':'Genre/Form (655)','Geographic Name (651)':'Geographic Name (651)','Occupation (656)':'Occupation (656)','Topical Term (650)':'Topical Term (650)','Uniform Title (630)':'Uniform Title (630)'};
    var named_entity_rule = {'Anglo-American Cataloguing Rules':'Anglo-American Cataloguing Rules','Describing Archives: A Content Standard':'Describing Archives: A Content Standard','Local':'Local'};
    var named_entity_type = {'Administrative History':'Administrative History','Biography':'Biography'};
   	var item_types = {'openapps_org_repository_1_0_artwork':'Artwork','openapps_org_repository_1_0_audio':'Audio','openapps_org_repository_1_0_correspondence':'Correspondence','openapps_org_repository_1_0_financial':'Financial','openapps_org_repository_1_0_journals':'Journals','openapps_org_repository_1_0_legal':'Legal','openapps_org_repository_1_0_manuscript':'Manuscript','openapps_org_repository_1_0_medical':'Medical','openapps_org_repository_1_0_memorabilia':'Memorabilia','openapps_org_repository_1_0_miscellaneous':'Miscellaneous','openapps_org_repository_1_0_notebooks':'Notebooks','openapps_org_repository_1_0_photographs':'Photographs','openapps_org_repository_1_0_printed_material':'Printed Material','openapps_org_repository_1_0_professional':'Professional','openapps_org_repository_1_0_research':'Research','openapps_org_repository_1_0_scrapbooks':'Scrapbooks','openapps_org_repository_1_0_video':'Video','openapps_org_repository_1_0_category':'Category','openapps_org_repository_1_0_item':'Item'};
    var item_levels = {"series":"Series","subseries":"Subseries","group":"Group","subgroup":"Subgroup","file":"File","item":"Item"};
    
    var location_types = {'area':'Area', 'building':'Building','floor':'Floor','room':'Room'};
    var container_types = {"":"","bin":"Bin","box":"Box","package":"Package","box_folder":"Box/Folder","carton":"Carton","cassette":"Cassette","disk":"Disk","drawer":"Drawer","envelope":"Envelope","folder":"Folder",
         					"frame":"Frame","map_case":"Map/Case","object":"Object","oversize":"Oversize","page":"Page","reel":"Reel","reel_frame":"Reel/Frame","volume":"Volume"};
    var extent_units = {"box":"Box","cubic_foot":"Cubic Foot","envelope":"Envelope","file":"File","folder":"Folder","item":"Item","linear_foot":"Linear Foot","object":"Object","package":"Package"};
    
    var named_entity_role = {'Actor':'Actor','Adapter':'Adapter','Animator':'Animator','Annotator':'Annotator','Applicant':'Applicant','Architect':'Architect','Arranger':'Arranger','Artist':'Artist','Assignee':'Assignee','Associated name':'Associated name','Attributed name':'Attributed name','Author':'Author','Author in quotations or text extracts':'Author in quotations or text extracts','Author of afterword, colophon, etc.':'Author of afterword, colophon, etc.','Author of dialog':'Author of dialog','Author of introduction':'Author of introduction','Author of screenplay, etc.':'Author of screenplay, etc.','Bibliographic antecedent':'Bibliographic antecedent','Binder':'Binder','Binding designer':'Binding designer','Book designer':'Book designer','Book producer':'Book producer','Bookjacket designer':'Bookjacket designer','Bookplate designer':'Bookplate designer','Calligrapher':'Calligrapher','Cartographer':'Cartographer','Censor':'Censor','Choreographer':'Choreographer','Cinematographer':'Cinematographer','Client':'Client','Collaborator':'Collaborator','Collotyper':'Collotyper','Commentator':'Commentator','Commentator for written text':'Commentator for written text','Compiler':'Compiler','Complainant':'Complainant','Complainant-appellant':'Complainant-appellant','Complainant-appellee':'Complainant-appellee','Composer':'Composer','Compositor':'Compositor','Conceptor':'Conceptor','Conductor':'Conductor','Consultant':'Consultant','Consultant to a project':'Consultant to a project','Contestant':'Contestant','Contestant-appellant':'Contestant-appellant','Contestant-appellee':'Contestant-appellee','Contestee':'Contestee','Contestee-appellant':'Contestee-appellant','Contestee-appellee':'Contestee-appellee','Contractor':'Contractor','Contributor':'Contributor','Copyright claimant':'Copyright claimant','Copyright holder':'Copyright holder','Corrector':'Corrector','Correspondent':'Correspondent','Costume designer':'Costume designer','Cover designer':'Cover designer','Creator':'Creator','Curator of an exhibition':'Curator of an exhibition','Dancer':'Dancer','Dedicatee':'Dedicatee','Dedicator':'Dedicator','Defendant':'Defendant','Defendant-appellant':'Defendant-appellant','Defendant-appellee':'Defendant-appellee','Degree grantor':'Degree grantor','Delineator':'Delineator','Depicted':'Depicted','Designer':'Designer','Director':'Director','Dissertant':'Dissertant','Distributor':'Distributor','Draftsman':'Draftsman','Dubious author':'Dubious author','Editor':'Editor','Electrotyper':'Electrotyper','Engineer':'Engineer','Engraver':'Engraver','Etcher':'Etcher','Expert':'Expert','Facsimilist':'Facsimilist','Film editor':'Film editor','First party':'First party','Forger':'Forger','Honoree':'Honoree','Host':'Host','Illuminator':'Illuminator','Illustrator':'Illustrator','Inscriber':'Inscriber','Instrumentalist':'Instrumentalist','Interviewee':'Interviewee','Interviewer':'Interviewer','Inventor':'Inventor','Landscape architect':'Landscape architect','Lender':'Lender','Libelant':'Libelant','Libelant-appellant':'Libelant-appellant','Libelant-appellee':'Libelant-appellee','Libelee':'Libelee','Libelee-appellant':'Libelee-appellant','Libelee-appellee':'Libelee-appellee','Librettist':'Librettist','Licensee':'Licensee','Licensor':'Licensor','Lighting designer':'Lighting designer','Lithographer':'Lithographer','Lyricist':'Lyricist','Manufacturer':'Manufacturer','Markup editor':'Markup editor','Metadata contact':'Metadata contact','Metal-engraver':'Metal-engraver','Moderator':'Moderator','Monitor':'Monitor','Musician':'Musician','Narrator':'Narrator','Opponent':'Opponent','Organizer of meeting':'Organizer of meeting','Originator':'Originator','Other':'Other','Papermaker':'Papermaker','Patent applicant':'Patent applicant','Patent holder':'Patent holder','Patron':'Patron','Performer':'Performer','Photographer':'Photographer','Plaintiff':'Plaintiff','Plaintiff-appellant':'Plaintiff-appellant','Plaintiff-appellee':'Plaintiff-appellee','Platemaker':'Platemaker','Printer':'Printer','Printer of plates':'Printer of plates','Printmaker':'Printmaker','Process contact':'Process contact','Producer':'Producer','Production personnel':'Production personnel','Programmer':'Programmer','Proofreader':'Proofreader','Publisher':'Publisher','Publishing director':'Publishing director','Puppeteer':'Puppeteer','Recipient':'Recipient','Recording engineer':'Recording engineer','Redactor':'Redactor','Renderer':'Renderer','Reporter':'Reporter','Research team head':'Research team head','Research team member':'Research team member','Researcher':'Researcher','Respondent':'Respondent','Respondent-appellant':'Respondent-appellant','Respondent-appellee':'Respondent-appellee','Responsible party':'Responsible party','Restager':'Restager','Reviewer':'Reviewer','Rubicator':'Rubicator','Scenarist':'Scenarist','Scientific advisor':'Scientific advisor','Scribe':'Scribe','Sculptor':'Sculptor','Second party':'Second party','Secretary':'Secretary','Set designer':'Set designer','Signer':'Signer','Singer':'Singer','Speaker':'Speaker','Sponsor':'Sponsor','Standards body':'Standards body','Stereotyper':'Stereotyper','Storyteller':'Storyteller','Surveyor':'Surveyor','Teacher':'Teacher','Thesis advisor':'Thesis advisor','Transcriber':'Transcriber','Translator':'Translator','Type designer':'Type designer','Typographer':'Typographer','Videographer':'Videographer','Vocalist':'Vocalist','Witness':'Witness','Wood-engraver':'Wood-engraver','Woodcutter':'Woodcutter','Writer of accompanying material':'Writer of accompanying material'};
    var named_entity_function = {'Creator':'Creator','Source':'Source','Subject':'Subject'};
    
    var datasource_types = {'smb':'Windows Network Share'};
   
    var import_processors = {'':'','openapps_org_repository_1_0_collection_0':'Default Text','openapps_org_repository_1_0_collection_1':'ArchiveManager XML','openapps_org_repository_1_0_collection_2':'Excel'};
    
    var classification_types = {'person':'Person','corporation':'Corporation','subject':'Subject'};
    
    var image_relationships = {'':'','avatar':'Avatar','gallery':'Gallery','rendition':'Rendition','thumbnail':'Thumbnail'};
     
    var weblink_types = {'avatar':'Avatar','rendition':'Rendition','navigation':'Navigation','partner':'Partner','zoomify':'Zoomify','finding_aid':'Finding Aid','flippingbook':'Flipping Book'};
    var webcontent_types = {'partnership':'Partnership','body':'Body'};
    
    var collection_task_types = {'General':'General'};
    var collection_activity_types = {'General':'General'};
    var collection_note_types = {'Abstract':'Abstract','Accruals note':'Accruals note','Appraisal note':'Appraisal note','Arrangement note':'Arrangement note','Bibliography':'Bibliography','Biographical/Historical note':'Biographical/Historical note','Chronology':'Chronology','Conditions Governing Access note':'Conditions Governing Access note','Conditions Governing Use note':'Conditions Governing Use note','Custodial History note':'Custodial History note','Dimensions note':'Dimensions note','Existence and Location of Copies note':'Existence and Location of Copies note','Existence and Location of Originals note':'Existence and Location of Originals note','File Plan note':'File Plan note','General note':'General note','General Physical Description note':'General Physical Description note','Immediate Source of Acquisition note':'Immediate Source of Acquisition note','Language of Materials note':'Language of Materials note','Legal Status note':'Legal Status note','Location note':'Location note','Material Specific Details note':'Material Specific Details note','Other Finding Aids note':'Other Finding Aids note','Physical Characteristics and Technical Requirements note':'Physical Characteristics and Technical Requirements note','Physical Facet note':'Physical Facet note','Preferred Citation note':'Preferred Citation note','Processing Information note':'Processing Information note','Related Archival Materials note':'Related Archival Materials note','Scope and Contents note':'Scope and Contents note','Separated Materials note':'Separated Materials note','Table of Contents':'Table of Contents','Text':'Text'};
	
	var search_types = {'openapps_org_contact_1_0_individual':'Individual','openapps_org_contact_1_0_organization':'Organization'}
	var contact_note_types = {'General note':'General note'}
	
	var service_path = '${serviceUrl}';
	
	var user = {'username':'${openapps_user.username}','fullname':'${openapps_user.firstName} ${openapps_user.lastName}'};
	var user_roles = ${roles};
	
	var height = '850px';
	var width = '1080px';
	var entityId = '${entityId}';
    </script> 
           	
	<div id="gwt" style="width:1080px;height:850px;"></div>
	<script type="text/javascript" language="javascript" src="/archivemanager-search-portlet/js/CollectionManager/CollectionManager.nocache.js"></script>