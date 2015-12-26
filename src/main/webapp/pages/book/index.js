window.onload = function () {
  var isbn = getQueryParam('isbn');
  if (isbn) {
    /*在这里添加函数,从数据库中获取信息,并填充到表单中




     */
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
