
$(":button").click(function() {
var isbn = this.id;
    $.ajax({
      url: '/library/v1/books/'+isbn+"?status=lost",
      type: 'PUT',
      success: function() {
        window.location.reload();
      }
    });
});
