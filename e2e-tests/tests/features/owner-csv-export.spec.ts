import { test, expect } from '@fixtures/base-test';
import * as path from 'path';
import * as fs from 'fs';

test.describe('Owner CSV Export', () => {
  test('downloads CSV with correct headers and data', async ({ page }) => {
    const [download] = await Promise.all([
      page.waitForEvent('download'),
      page.goto('/owners.csv?lastName=Davis'),
    ]);

    expect(download.suggestedFilename()).toBe('owners.csv');

    const downloadPath = path.join(__dirname, '../../test-results', 'owners-test.csv');
    await download.saveAs(downloadPath);

    let content = fs.readFileSync(downloadPath, 'utf8');
    // Strip BOM if present
    if (content.charCodeAt(0) === 0xfeff) {
      content = content.slice(1);
    }

    const lines = content.trim().split(/\r?\n/);
    // Header row present
    expect(lines[0]).toContain('Name');
    expect(lines[0]).toContain('Telephone');
    // Data rows present
    expect(lines.length).toBeGreaterThan(1);
    expect(content).toContain('Davis');

    fs.unlinkSync(downloadPath);
  });

  test('returns header row only when no owners match', async ({ page }) => {
    const [download] = await Promise.all([
      page.waitForEvent('download'),
      page.goto('/owners.csv?lastName=ZZZNoSuchOwner'),
    ]);

    const downloadPath = path.join(__dirname, '../../test-results', 'owners-empty-test.csv');
    await download.saveAs(downloadPath);

    let content = fs.readFileSync(downloadPath, 'utf8');
    if (content.charCodeAt(0) === 0xfeff) {
      content = content.slice(1);
    }

    const lines = content
      .trim()
      .split(/\r?\n/)
      .filter((l) => l.trim().length > 0);
    expect(lines).toHaveLength(1);
    expect(lines[0]).toBe('Name,Address,City,Telephone');

    fs.unlinkSync(downloadPath);
  });
});
