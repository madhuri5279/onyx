(ns ^:no-doc onyx.messaging.aeron.peer-manager
  "Fast way to multiplex to short ids"
  (:refer-clojure :exclude [assoc dissoc get])
  (:require [taoensso.timbre :refer [fatal info] :as timbre])
  (:import [org.agrona.collections Int2ObjectHashMap Int2ObjectHashMap$KeyIterator Int2ObjectHashMap$EntryIterator])) 

;; Note, slow to assoc/dissoc as it makes a complete clone, but this only happens on reallocations
;; Very fast to get the object given an int
(defprotocol IntObjectMap
  (clone [this]))

(deftype VPeerManager [^Int2ObjectHashMap m]
  clojure.lang.Associative
  (assoc [this k v]
    (let [vp ^VPeerManager (clone this)]
      (.put ^Int2ObjectHashMap (.m vp) (int k) v)
      vp))
  clojure.lang.ILookup
  (valAt [this k]
    (.get m (int k)))
  (valAt [this k default]
    (or (.valAt this k) default))
  clojure.lang.IPersistentMap
  (without [this k]
    (let [vp ^VPeerManager (clone this)]
      (.remove ^Int2ObjectHashMap (.m vp) (int k))
      vp))
  IntObjectMap
  (clone [this]
    (VPeerManager.
     (let [iterator ^Int2ObjectHashMap$EntryIterator (.iterator (.entrySet ^Int2ObjectHashMap (.m this)))
           new-hm ^Int2ObjectHashMap (Int2ObjectHashMap.)]
       (while (.hasNext iterator)
         (let [kv ^Int2ObjectHashMap$EntryIterator (.next iterator)
               k ^java.lang.Integer (.getKey kv) 
               v (.getValue kv)]
           (.put new-hm k v)))
       new-hm))))

(defn vpeer-manager []
  (VPeerManager. (Int2ObjectHashMap.)))

(comment (def a (reduce (fn [m i]
                          (assoc m i {:hi :there}))
                        (vpeer-manager)
                        (range 20))) 

         (def b (reduce (fn [m i]
                          (assoc m i {:hi :there}))
                        {}
                        (range 20))))
