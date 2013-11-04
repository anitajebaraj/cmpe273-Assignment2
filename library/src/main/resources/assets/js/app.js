
$(":button").click(function() {
var isbn = this.id;
alert('hkjgjk')
    $.ajax({
      url: '/library/v1/books/'+isbn+"?status=lost",
      type: 'PUT',
      data: "status=lost",
      success: function() {
        alert('Load was performed.');
        window.location.reload();
      }
    });
});
