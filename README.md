# Space-Time Tagger

This is an Android app which allows you to 'tag' the current time (and optionally your current
location) and store these tags in a list for later review.
Broadly, it's similar to the structure of Google Keep, where you can manage a list of lists.

The motivating use case is if you're filming something over a long period of time and want a list of
key moments to revisit afterwards, without having to skim through the whole video.

It is a work-in-progress.

## Use

The app is not (currently) directly available anywhere, but you can clone/fork this repository and
install it on an android phone with developer mode enabled.

## Features

### Current

- Sessions List screen to see/add/delete sessions.
- Session Detail screen to see/add/delete tags within a session.
- Settings screen to toggle whether current location is stored in tags (requesting permission if
  necessary).

Note: No data is persisted after the app closes, and the current location settings is ignored (
location is never captured). This will change eventually.

### Potential Future

See [enhancements](https://github.com/kr-matthews/space-time-tagger/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement)
on GitHub.

- Persist session data.
- Integration tests.
- Add location to tags (when the settings is enabled).
- Map view of tags.
- Improve styling and colours.
- Ability to create tags from phone's lock screen (and/or a mode to prevent screen from locking).
- Settings for default session names.
- Ability to 'start' a session (introducing a relative timestamp, in addition to the absolute
  timestamp).
- Ability to mark sessions and/or tags as complete/archived (rather than deleting them).

## Original Intentions

- Build an Android app from scratch - first personal Android project.
- Get more familiar with basic architecture and implement best practices, including tests - prior
  experience has all been modifying _existing_ apps.
- A relatively basic, but still useful Android app.

## Focus

- Android architecture.
- Clean code.
- Tests.
- Clean git history, with a functional app at all points.
- Android permissions - location in particular.
- Allowing actions via phone lock screen.

## Flaws

See the [issues](https://github.com/kr-matthews/space-time-tagger/issues) on GitHub, in addition to
the following:

- (currently too early to list flaws, but I'm sure there will be many soon enough)
