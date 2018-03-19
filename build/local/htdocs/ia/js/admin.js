//var organisation="acme";

    	$(document).ready(function(){

        	demo.initChartist();

      	$.notify({
            		message: "Welcome to <b>Perfios Admin Page</b>"

            },{
                type: 'info',
                timer: 4000
            });
            $.ajax({

                                    url : '/IAServer/ia/api/v1/alogin/dashboard_dropdown',
                                    method :"POST",
                                    data : {},
                                    datatype:'json',
                                    success : function(response){

                          console.log("login --> ", response);
                          var textnode=document.getElementById("drop");
                          var newOption;
                          for(i=0;i<response.length;i++)
                          {

                              var val=response[i].organisation_id+"-"+response[i].organisation_name;
                               newOption += ('<option >'+val+'</option>');
                              console.log(newOption);
                          }
                          $('#drop').html(newOption);

                    	 },
                    	 error : function(e){
                         alert("Something went wrong, try later");
                    	 }
                      });

    	});


   function submitForm() {
      var org_id= document.getElementById("drop").value;
      console.log(org_id);
      var email = document.getElementById("email").value;
      var pass = document.getElementById("password").value;
      var form=$("#form1");
      console.log("hello");
      if(org_id && org_id.length>0 && pass && pass.length>0){
        var data=$(form).serialize();
          //var json = JSON.stringify(data);
          $.ajax({
             url : '/IAServer/ia/api/v1/alogin/dashboard',
             method :"POST",
             data : data,
             datatype:'json',
             success : function(response){
              console.log("login --> ", response);
             if(response.success == true){
               // window.location.href="dashboard.html";
                alert("User Added Successfully");
             }else{
               alert(response.message);
             }
        	 },
        	 error : function(e){
             alert("Something went wrong, try later");
        	 }
          });

      }else{
        alert("Please enter credentials");
      }
  }
   function submitForm1() {

      var org_name = document.getElementById("org").value;
      var form=$("#form2");
      if(org_name && org_name.length>0){
        var data=$(form).serialize();
          //var json = JSON.stringify(data);
          $.ajax({
             url : '/IAServer/ia/api/v1/alogin/dashboard_org',
             method :"POST",
             data : data,
             datatype:'json',
             success : function(response){
              console.log("login --> ", response);
             if(response.success == true){
                alert("Organisation Added Successfully");
                window.location.href="dashboard.html";

             }else{
               alert(response.message);
             }
        	 },
        	 error : function(e){
             alert("Something went wrong, try later");
        	 }
          });

      }else{
        alert("Please enter credentials");
      }
  }


function logout(){
console.log("haofhcoaubfciascf");

$.ajax({
             url : '/IAServer/ia/api/v1/adminLogout',
             method :"POST",
             data : {},
             datatype:'json',
             success : function(response){
              console.log("login --> ", response);
             if(response.success == true){
                alert("Logged Out Successfully");
                window.location.href="alogin.html";

             }else{
               alert(response.message);
             }
        	 },
        	 error : function(e){
             alert("Something went wrong, try later");
        	 }
          });


}