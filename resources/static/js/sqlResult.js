$(document).ready(function () {
    var table = $('#table').DataTable(
        {
            "language": {
                "url": "static/datatables/Russian.json"
            },
            dom: "<'row'<'col-sm-4 align_left'l><'col-sm-4 align_center'B><'col-sm-4'f>>R" +
            "<'row'<'col-sm-12'tr>>" +
            "<'row'<'col-sm-5 align_left'i><'col-sm-7'p>>",
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
                        rows: {selected: true}
                    }
                }
            ],
            select: {
                style: 'multi'
            },
            initComplete : function () {
                var api = this.api();
                api.buttons().container()
                    .appendTo( $('.buttons'));
            },
            'order': [[1, 'asc']]
        });
});

