(ns vquiz.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :quiz-title
 (fn [db]
   (:quiz-title db)))

(re-frame/reg-sub
 :videos
 (fn [db]
   (:videos db)))
