export function Welcome({ onDemo, loading }: { onDemo: () => void; loading: boolean }) {
  return (
    <section className="card welcome">
      <h2>Welcome</h2>
      <p>
        Record each night — when you slept, how well, and optional habits like screen time or exercise. After a few
        nights you’ll see how regular your schedule is and which habits go with your best sleep. After a week, a
        model trained on your own nights predicts how well you’ll sleep tonight.
      </p>
      <div className="actions">
        <button onClick={onDemo} disabled={loading}>{loading ? 'Loading…' : 'Explore with 60 example nights'}</button>
        <span className="muted">or log your first night below.</span>
      </div>
    </section>
  )
}
