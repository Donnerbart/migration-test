# Usage

- Start Constants.CLUSTER_SIZE instances of ActiveMember
- Start WarmupClient
- Wait until data is filled in and queries have started
- Start PassiveMember and stop it again (or use PassiveMemberWithAutoShutdown)
