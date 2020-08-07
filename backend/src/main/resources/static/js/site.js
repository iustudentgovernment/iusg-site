(function (window, document, $, undefined) {
    $(document).ready(function() {

        Foundation.OffCanvas.defaults.transitionTime = 5000;
        Foundation.OffCanvas.defaults.forceTop = false;
        Foundation.OffCanvas.defaults.positiong = 'right';

        Foundation.Accordion.defaults.multiExpand = true;
        Foundation.Accordion.defaults.allowAllClosed = true;

        $(document).foundation();

        var IU = window.IU || {};

        /* Delete modules if necessary (prevents them from auto-initializing) */
        // delete IU.uiModules['accordion'];

        /*
         * Initialize global IU & its modules
         * Custom settings can be passsed here
         */
        IU.init && IU.init({debug: true});
    });
})(window, window.document, jQuery);