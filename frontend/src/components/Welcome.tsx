export function Welcome({ onDemo, loading }: { onDemo: () => void; loading: boolean }) {
  return (
    <section className="card welcome">
      <h2>Getting started</h2>
      <p>
        Add a night with when you slept and how well. You can also note habits like screen time or exercise. After a
        few nights you’ll see how regular your schedule is, and after about a week it can guess how well you’ll sleep
        tonight based on your past nights.
      </p>
      <div className="actions">
        <button onClick={onDemo} disabled={loading}>{loading ? 'Loading…' : 'Try it with 60 example nights'}</button>
        <span className="muted">or log your first night below.</span>
      </div>
    </section>
  )
}
