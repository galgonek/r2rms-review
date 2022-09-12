define(`if', `ifelse(`$1', `$2', `$3', `dnl')')dnl
define(`ifnot', `ifelse(`$1', `$2', `dnl', `$3')')dnl
define(`apostrophe', changequote([,])[changequote([,])'changequote(`,')]changequote(`,'))dnl
dnl
define(`QNAME', ifelse(SERVER, `virtuoso', `nextprot.nextprot.`$1'', `nextprot.`$1''))dnl
define(`IDXPARAMS', ifelse(SERVER, `postgresql', ` with(fillfactor=100)', `'))dnl
define(`UNIQUE', ifelse(SERVER, `virtuoso', `', `unique`'IDXPARAMS() '))dnl
define(`WHERE', `')dnl
dnl
define(`KEYCHAR', ifelse(SERVER, `mariadb', `varchar(`$1')', `varchar'))dnl
define(`KEYTYPE', ifelse(MODE, `indirect', `integer', `KEYCHAR(`$1')'))dnl
define(`KEYTYPE_SIZE', ifelse(MODE, `indirect', `4B', `xB'))dnl
define(`LONGVAR', ifelse(SERVER, `virtuoso', `long varchar', ifelse(SERVER, `mariadb', `text', `varchar')))dnl
define(`VARCHAR', ifelse(SERVER, `mariadb', `varchar(`$1')', `varchar'))dnl
define(`XSDBOOLEAN', ifelse(SERVER, `virtuoso', `smallint', `boolean'))dnl
define(`XSDINTEGER', ifelse(MAPPING, `ontop', `bigint', `numeric'))dnl
define(`XSDINTEGER_SIZE', ifelse(MAPPING, `ontop', `8B', `xB'))dnl
dnl
define(`SPARQL', ifelse(SERVER, `virtuoso', ```"SPARQL"''', ifelse(SERVER, `mariadb', `sparql@localhost', `sparql')))dnl
