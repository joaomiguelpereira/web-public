
/**
 * Register event handler for type of business. Do it when page is loaded
 */



$(window).load(function () {
	//focus the first field with errors

	
	//In case of reload from validation errors, select the correct type and show the form
	if ( $("input[name=office.businessType]").val() != "" ) {
		var type = $("input[name=office.businessType]").val();
		if ( $("#"+type+"Choice") ) {
			$("#"+type+"Choice").attr('checked', true );
			updateForm(type);
			FormUtils.focusFirstErrorField();
		}
	}
	
	//Add event listener from change events
	$("input[name=businessTypeChoice]").change( function() { 
		updateForm($("input[name=businessTypeChoice]:checked").val());
		
	});
	
	
	
});



/**
 * Show and update the form
 * @param type the Business typr
 */
function updateForm(type) {
	//show the form
	$('#preRegisterOfficeForm').show();
	//change occurences
	$('.businessTypeWithPreposition').html(names[type][2]);
	$('.businessType').html(names[type][0]);
	$('.businessTypeWithPronoum').html(names[type][1]);
	$("input[name=office.businessType]").val(type);	
}