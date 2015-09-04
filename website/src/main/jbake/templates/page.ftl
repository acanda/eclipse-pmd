<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${content.title}</title>

        <link href="css/bootstrap-3.3.5.min.css" rel="stylesheet">
        <link href="css/bootstrap-theme-3.3.5.min.css" rel="stylesheet">
        <link href="css/lightbox-2.8.1.css" rel="stylesheet">
        <link href="css/layout.css" rel="stylesheet">

        <script type="text/javascript">
            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', 'UA-37305934-1']);
            _gaq.push(['_trackPageview']);

            (function() {
                var ga = document.createElement('script');
                ga.type = 'text/javascript';
                ga.async = true;
                ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                var s = document.getElementsByTagName('script')[0];
                s.parentNode.insertBefore(ga, s);
            })();
        </script>
        <script src="js/jquery-2.1.4.min.js"></script>
    </head>
    <body>
        <nav class="navbar navbar-default navbar-fixed-top">
            <div class="container">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="index.html">eclipse-pmd</a>
                </div>
                <div id="navbar" class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                       <li>
                            <a href="getting-started.html">Get started</a>
                        </li>
                        <li>
                            <a href="documentation.html">Documentation</a>
                        </li>
                        <li>
                            <a href="changelog.html">Changelog</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="container">
            ${content.body}
        </div>
        <footer class="footer">
            <div class="container">
                <div class="row">
                    <div class="col-md-4">
                        <p>
                            &copy; 2012 &ndash; ${config.buildYear} Philip Graf. All Rights Reserved.
                            <br />
                            eclipse-pmd uses <a href="https://pmd.github.io/">PMD</a>.
                        </p>
                    </div>
                    <div class="col-md-4 col-md-offset-4">
                        <p>
                            Github: <a href="https://github.com/acanda/eclipse-pmd/">eclipse-pmd</a>
                            <br />
                            Twitter: <a href="https://twitter.com/eclipsepmd">@eclipsepmd</a>
                        </p>
                    </div>
                </div>
            </div>
        </footer>
        <div id="forkme">
            <a href="https://github.com/acanda/eclipse-pmd/">Fork me on GitHub</a>
        </div>
        <script src="js/bootstrap-3.3.5.min.js"></script>
        <script src="js/lightbox-2.8.1.min.js"></script>
        <script>
            lightbox.option({
                'resizeDuration': 200,
                'positionFromTop': 100
            })
        </script>
    </body>
</html>