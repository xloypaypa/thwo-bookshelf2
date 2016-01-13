window.onload = function () {
  var isbn = getQueryParam('isbn');
  if (isbn) {

    httpRequest('GET', baseUrl + '/' + isbn, function (book) {
             document.querySelector('[name=Title]').value = book.title;
             document.querySelector('[name=Author]').value = book.author;
             var isbn = document.querySelector('[name=ISBN]');
             isbn.value = book.isbn;
             isbn.disabled="true";
             document.querySelector('[name="Price($)"]').value = book.price;
        });
  }

  var form = document.querySelector('.form');
  form.addEventListener('submit', function (e) {
    e.preventDefault();
    var formElements = e.target.elements;
    var book = {};
    for (var i = 0; i < formElements.length - 1; ++i) {
      book[tableHeaderMapper[formElements[i].name]] = formElements[i].value;
    }
    if (isbn) {
      var para = document.createElement("p");
      httpRequest('PUT', baseUrl + '/' + isbn, function () {
        location.href = '/index.html';
      }, book);
    } else {
      httpRequest('POST', baseUrl, function () {
        location.href = '/index.html';
      }, book);
    }
  });
};
