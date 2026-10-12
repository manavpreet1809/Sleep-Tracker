import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ApiError, type Night, type api } from '../api'
import { NightForm } from './NightForm'

function setup(saveNight: (night: Night) => Promise<Night>) {
  const client = { saveNight } as unknown as ReturnType<typeof api>
  const onSaved = vi.fn()
  render(
    <QueryClientProvider client={new QueryClient()}>
      <NightForm client={client} editing={null} onSaved={onSaved} onCancel={() => {}} />
    </QueryClientProvider>,
  )
  return { onSaved }
}

test('suggests hours asleep from the schedule and sends blank habits as null', async () => {
  const saveNight = vi.fn(async (night: Night) => night)
  const { onSaved } = setup(saveNight)
  const user = userEvent.setup()

  const bedtime = screen.getByLabelText('Fell asleep')
  await user.clear(bedtime)
  await user.type(bedtime, '00:30')
  expect(screen.getByLabelText('Hours asleep')).toHaveValue(6.25)

  await user.type(screen.getByLabelText(/Screen time/), '2.5')
  await user.click(screen.getByRole('button', { name: 'Save night' }))

  const sent = saveNight.mock.calls[0]![0]
  expect(sent).toMatchObject({ bedtime: '00:30', wakeTime: '07:00', sleepHours: 6.25, screenTimeHours: 2.5, cardioMinutes: null })
  expect(onSaved).toHaveBeenCalled()
})

test('shows the server’s message for an invalid field', async () => {
  setup(async () => { throw new ApiError(400, 'Some values are invalid', { waterIntakeMl: 'must be less than or equal to 20000' }) })
  const user = userEvent.setup()
  await user.type(screen.getByLabelText(/Water/), '30000')
  await user.click(screen.getByRole('button', { name: 'Save night' }))
  expect(await screen.findByText('must be less than or equal to 20000')).toBeInTheDocument()
  expect(screen.getByLabelText(/Water/)).toHaveAttribute('aria-invalid', 'true')
})
