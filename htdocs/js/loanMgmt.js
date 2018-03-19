
var organisation = "acme";

function generateLink(){

  var reqData = {
     "emailId" : $("#email").val(),
     "name" : $("#name").val(),
     "mobileNumber" : $("#mobNum").val(),
     "type" : "IA",
     "organisation" : organisation,
     "loanType" : $("#loanType").val(),
     "loanAmount" : $("#loanAmount").val(),
     "loanDuration" : $("#loanDuration").val(),
   }

   var json = JSON.stringify(reqData);

      $.ajax({
              url :"/IAServer/ia/api/v1/generateUrl",
              method : "POST",
              dataType : "json",
              data : {
                  "json" : json,
                  "organisation" : organisation
              },
              success : function(response) {
                if(response.success){
                  showNotification(response.message);
                  //$('#loanFormID').trigger("reset");
                  cancel();
                }else{
                  showNotification(response.message);
                }
              },
              error : function(response){
                  console.log(response);
              }
       });
    return false;
}

function requestForLoan() {
  $("#loanView").hide();
  $("#loanFormDiv").show();
  document.getElementById('pageHeaderTxt').innerText = ' Loan Application Request';

}

function cancel() {
  $("#loanView").show();
  $("#loanFormDiv").hide();
  document.getElementById('pageHeaderTxt').innerText = ' Loan Application View';
  viewLoans();
}

function buildJQueryReportDataTable(tableID, reportData, columns, hiddenColumns, stickyHeader, rowSelectColoring){
    try{
        var reportTable = $(tableID);
        reportTable.dataTable().fnDestroy();
        reportTable.DataTable({
            data: reportData,
            processing: true,
            responsive: true,
            // dom: "<'row'<'col-sm-4'B><'col-sm-3'f><'col-sm-2 col-sm-offset-3'l>>" +
            //      "<'row'<'col-sm-12'tr>>" + "<'row'<'col-sm-5'i><'col-sm-7'p>>",
            // buttons: [
            //     {
            //         extend: 'colvis',
            //         text: '<i class="fa fa-eye" aria-hidden="true"></i>',
            //         titleAttr: 'Column Hide/Show'
            //     },
            //     {
            //         extend: 'excel',
            //         text: '<i class="fa fa-file-excel-o" aria-hidden="true"></i>',
            //         titleAttr: 'Export to Excel'
            //     }
            // ],
            order: [[ 0, "desc" ]],
            columns: columns
        });

        if(hiddenColumns) $(tableID).DataTable().columns(hiddenColumns).visible(false);
        if(stickyHeader) new $.fn.dataTable.FixedHeader(reportTable);

        // Row selection coloring
        if(rowSelectColoring){
            $('#transactionTableID tbody').on( 'click', 'tr', function () {
                if($(this).hasClass('selected')) {
                    $(this).removeClass('selected');
                } else {
                    reportTable.$('tr.selected').removeClass('selected');
                    $(this).addClass('selected');

                }
            });
        }
    }catch(e){};
}

function viewLoans() {
    var reqData = {
        "organisation" : organisation,
        "fromDate" : new Date(),
        "toDate" : new Date(),
        "type": "IA"
    };

    $.ajax({
        url :"/IAServer/ia/api/v1/fetchData",
        method : "POST",
        dataType : "json",
        data : {
          "json" : reqData,
          "organisation" : organisation
        },
        success : function(response) {
            // console.log("viewLoans", response);
            var data = JSON.parse(response.message);
            if (data.length != 0) {

              async.series([
                  function(callback) {

                      async.forEachOf(data, function (value, index) {
                        value.transactionIdLink = "<a href='#' onclick='viewFormData(\"" + index + "\");'><span>" + value["transactionId"] + "</span></a>";
                        if(value["reportLocation"])
                          value.reportLocationUrl = "<a href='/IAServer/ia/api/v1/downloadReport/" + organisation + "/" + value["transactionId"] + "'" + " target='_blank'><span><i class='fa fa-cloud-download fa-2x' aria-hidden='true' style='display:inline' data-toggle='tooltip' data-placement='top' title='Report File'></i></a>";
                        else
                          value.reportLocationUrl = "";

                        if(value["formDataJson"])
                          value.formDataJsonObj = JSON.parse(value["formDataJson"]);

                        if(index === (data.length-1)){
                          callback(null, 'one');
                        }

                      }, function (err) {});
                  },
                  function(callback) {

                    buildJQueryReportDataTable('#transactionTableID', data,
                        [
                            { "data": "id" },
                            { "data": "transactionIdLink" },
                            { "data": "perfiosTranscationId" },
                            { "data": function(row, type, set){
                                  if ( type === 'display' ) {
                                      return row.formDataJsonObj.name + '<br/><p>' + row.formDataJsonObj.emailId + '</p>';
                                  } else {
                                    return null;
                                  }
                            }},
                            { "data": "formDataJsonObj.mobileNumber" },
                            { "data": "expiryDate" },
                            { "data": function(row, type, set){
                                  if ( type === 'display' ) {
                                      return row.status + '<br/><p><small>' + row.message + '</small></p>';
                                  } else {
                                    return null;
                                  }
                            }},
                            { "data": function(row, type, set){
                                  if ( type === 'display' ) {
                                      return '<center>' + row.reportLocationUrl + '</center>';
                                  } else {
                                    return null;
                                  }
                            }},
                            { "data": function(row, type, set){
                                  if ( type === 'display' ) {
                                      return row.created + '<br/>' + row.updated;
                                  } else {
                                    return null;
                                  }
                            }}
                        ], [0], null, true
                    );

                    callback(null, 'three');
                  }
              ],
              function(err, results) {});

            } else if (data.length == 0) {
                showNotification("No data for specified request", "info");
            }
        },
        error : function(response) {
           if(response.message)
              aleshowNotificationrt(response.message, "error");
           else
              showNotification("Couldn't able to connect!", "error");
        }
    });

    return false;
}

$( document ).ready(function() {
    cancel();
});

var getJQueryDataTableRowData = function(tableId, rowIndex){
    var table = $(tableId).DataTable();
    return table.row(rowIndex).data();
}

function viewFormData(rowIndex){
  loanObj = getJQueryDataTableRowData('#transactionTableID', rowIndex);
  if (loanObj) {
      $('#viewFormDataModel').modal('show');
      $("#transactionTitleID").text("Loan Detail - " + loanObj.transactionId);
      $("#vLoanType").text(loanObj.formDataJsonObj.loanType);
      $("#vLoanAmount").text(loanObj.formDataJsonObj.loanAmount);
      $("#vLoanDuration").text(loanObj.formDataJsonObj.loanDuration);
  }
  return false;
}

function logout() {
    $.ajax({
    	 url : '/IAServer/ia/api/v1/logout',
    	 method :"POST",
    	 dataType : "json",
    	 data : {
    		 "organisation" : organisation
    	 },
    	 success : function(response){
        if(response.success){
            window.location.href="login.html";
        }
       },
    	 error : function(e){
         showNotification("Couldn't able to connect!", "error");
    	 }
   });
}
