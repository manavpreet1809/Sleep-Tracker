import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import type { HabitResult } from '../api'
import { Habits } from './Habits'

const row = (description: string, difference: number | null): HabitResult => ({
  key: description, description, nightsWith: 4, nightsWithout: 3,
  averageWith: difference === null ? null : 6 + difference, averageWithout: difference === null ? null : 6, difference,
})

test('shows signed differences and switches outcome', async () => {
  render(<Habits quality={[row('Cardio', 1.5), row('Screens', -0.75)]} duration={[row('Water', 0.5)]} />)
  expect(screen.getByText('+1.5')).toBeInTheDocument()
  expect(screen.getByText('−0.8')).toBeInTheDocument()
  await userEvent.setup().click(screen.getByRole('button', { name: 'Duration' }))
  expect(screen.getByText('+0 h 30 min')).toBeInTheDocument()
})

test('explains when a habit cannot be compared', () => {
  render(<Habits quality={[row('Naps', null)]} duration={[]} />)
  expect(screen.getByText('Not enough variety to compare yet')).toBeInTheDocument()
})
