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
            dom: "<'row'<'col-sm-3'f><'col-sm-6 align_center'B>>" +
            "tr" +
            "<'row'<'col-sm-5 align_left'i>>",
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
                    text: 'Выгрузить как CSV',
                    exportOptions: {
                        rows: {selected: true},
                        columns: [1, 2, 3, 4, 5, 6],
                        format: {
                            body: function (data, row, column, node) {
                                return column === 4 && node.childNodes[0] ? node.childNodes[0].href : data;
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