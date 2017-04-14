(ns vquiz.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [vquiz.core-test]))

(doo-tests 'vquiz.core-test)
