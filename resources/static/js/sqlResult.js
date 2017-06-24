$(document).ready(function () {
    $('#table').DataTable(
        {
            "paging": false,
            fixedHeader: {
                header: true,
                headerOffset: 45
            },
            "language": {
                "url": "static/datatables/Russian.json"
            },
            dom: "<'row'<'col-3'f><'col-6 align_center'B>>" +
            "tr" +
            "<'row'<'col-5 align_left'i>>",
            'columnDefs': [
                {
                    'targets': 0,
                    'checkboxes': {
                        'selectRow': true
                    }
                }
            ],
            buttons: [
                {
                    extend: 'csv',
                    charset: 'utf-8',
                    bom: true,
                    text: 'Выгрузить выбранное как CSV',
                    exportOptions: {
                        rows: {selected: true},
                        columns: [1, 2, 3, 4, 5, 6, 7],
                        format: {
                            body: function (data, row, column, node) {
                                switch (column) {
                                    case 1:
                                    case 3:
                                        return node.childNodes[0].text;
                                    case 2:
                                        if (node.childNodes[0]) return node.childNodes[0].href;
                                        break;
                                }
                                return data;
                            }
                        }
                    }
                }
            ],
            select: {
                style: 'multi'
            },
            initComplete: function () {
                this.api().buttons().container()
                    .appendTo($('.buttons'));
            },
            'order': [[1, 'asc']]
        });
});