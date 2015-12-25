var Header = document.querySelector('#Header');
document.body.appendChild(Header.import.querySelector('.g-head'));

var tableElements = [];

window.onload = function () {
    var booksList = document.querySelector('tbody');

    var createRow = function (book) {
        var tr = document.createElement('tr');

        if (!book) {
            return tr;
        }

        for (var key in tableHeaderMapper) {
            var td = document.createElement('td');
            td.textContent = book[tableHeaderMapper[key]];
            td.setAttribute('class', 'col-' + tableHeaderMapper[key]);
            tr.appendChild(td);
        }

        var td = document.createElement('td');
        var editBtn = document.createElement('a');
        editBtn.textContent = 'edit';
        editBtn.setAttribute('class', 'button');
        editBtn.setAttribute('href', '/pages/book/index.html?isbn=' + book[tableHeaderMapper.ISBN]);
        td.appendChild(editBtn);

        var deleteBtn = document.createElement('a');
        deleteBtn.textContent = 'delete';
        deleteBtn.setAttribute('class', 'button');

        /*在这里添加点击删除事件监听




        */

        td.appendChild(deleteBtn);

        td.setAttribute('class', 'col-operates');
        tr.appendChild(td);


        return tr;
    };

    httpRequest('GET', baseUrl, function (books) {
        if (!books.length) {
            booksList.appendChild(createRow());
        } else {
            books.forEach(function (book) {
                var tr = createRow(book);
                tableElements.push(tr);
                booksList.appendChild(tr);
            });
        }
    });
};
