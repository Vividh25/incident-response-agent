# Incident Response Agent

A generic, pluggable AI agent that consumes Kafka-based failure alerts from
any producing service, diagnoses the problem via a ReAct tool-calling loop,
proposes a fix, waits for human approval, then executes the remediation on
Kubernetes.

Built to learn agentic design patterns (ReAct, planner/executor, reflection,
human-in-the-loop, tool-use) hands-on, and to get real Docker/Kubernetes/Helm
deployment experience along the way.

## Modules

- **toy-target-app** — a deliberately unreliable demo service. It has
  endpoints to switch on an error spike or a latency spike, and a scheduled
  `HealthMonitor` that watches its own request outcomes and publishes an
  `AlertEvent` to Kafka when the error rate crosses a threshold. Stands in
  for "any real production service" so the agent has something to react to.
- **agent-service** — (coming next) consumes `AlertEvent`s, runs the
  diagnostic ReAct loop, proposes a remediation, gates it behind human
  approval, and executes it.

## The event contract

Every producing service — real or toy — publishes to the `incident-alerts`
Kafka topic using the same shape:

```json
{
  "source": "toy-target-app",
  "severity": "critical",
  "metric": "error_rate",
  "value": 0.42,
  "threshold": 0.3,
  "timestamp": "2026-08-04T10:15:30Z",
  "context": { "window_requests": "50", "window_failures": "21" }
}
```

The agent-service only ever depends on this contract — never on any specific
producer's internals. That's what makes it reusable across applications
instead of tied to one company's stack.

## Roadmap

1. ✅ Toy target app + health monitor + Kafka alert publishing
2. Agent-service skeleton consuming alerts (Kafka listener)
3. Diagnostic agent — ReAct loop with log/metric query tools
4. Human-in-the-loop approval gate (simple REST endpoint + pending-actions store)
5. Remediation agent — executes approved fix via a Kubernetes tool
6. Reflection step — verify the fix actually worked
7. Containerize both services, write Helm charts, deploy to a local cluster
8. GitHub Actions CI/CD — build and push images on every push

## Running the toy target app locally

Start Kafka (and a UI to inspect topics):

```bash
docker compose up -d
```

Then, from `toy-target-app/`:

```bash
mvn spring-boot:run
```

Generate some traffic:

```bash
# normal traffic
curl http://localhost:8081/work

# turn on a 60%-of-requests error spike
curl -X POST "http://localhost:8081/simulate/error-spike?enabled=true"

# hit /work a bunch of times (a simple loop works fine)
for i in $(seq 1 20); do curl -s http://localhost:8081/work > /dev/null; done

# reset
curl -X POST http://localhost:8081/simulate/reset
```

Within ~10 seconds of the error rate crossing 30%, watch the `incident-alerts`
topic in Kafka UI at http://localhost:8080 — you should see an `AlertEvent`
land. That event is what the agent-service will consume next.
