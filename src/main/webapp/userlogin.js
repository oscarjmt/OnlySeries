$(document).ready(function(){
    $("#sign-in-btn").click(function(){
        
          $.ajax( {
			
			type: "GET",
			url: '/OnlySeries/HelloServlet?email=' + document.getElementById("floatingInput").value,
			success: function(data) {
				$.each(data.series, function(i,item){
					  $('#img'+i).attr('src', 'images/'+item+'.jpg');
				});
			}
		});

    });


});