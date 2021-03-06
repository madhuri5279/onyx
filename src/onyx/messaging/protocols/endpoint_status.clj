(ns onyx.messaging.protocols.endpoint-status)

(defprotocol EndpointStatus
  (start [this])
  (stop [this])
  (ready? [this])
  (info [this])
  (statuses [this])
  (set-replica-version! [this new-replica-version])
  (set-endpoint-peers! [this new-peers])
  (min-endpoint-epoch [this])
  (timed-out-subscribers [this])
  (liveness [this])
  (poll! [this]))
