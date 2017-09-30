<div id="search_collection">
	<form action="search" onsubmit="return processSearchForm(this,event);">
		<div style="height:3px;"></div>
		<table id="formtable">
			<tr>
				<td><input name="query" type="text" class="searchtext1" /></td>
				<td><input type="image" style="padding-top:4px;" src="<%= themeDisplay.getPathThemeImages()%>/<%= button%>" alt="Submit button" /></td>
				<td><input name="sis" type="checkbox" /></td>
				<td>Search In Search &nbsp;</td>
				<!--<td>&nbsp;&nbsp;? <a href="search/help">Help</a></td>-->
			</tr>
		</table>
	</form>
</div>