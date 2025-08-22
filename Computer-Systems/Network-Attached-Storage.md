# Network Attached Storage (NAS) Documentation

## What is NAS?

Network Attached Storage (NAS) is a storage system that connects to a network, enabling multiple devices to access and share files without needing physical connections like USB drives. Unlike an external hard drive that plugs into a single machine, NAS acts as a **central hub** for storing, retrieving, and sharing files over a local area network (LAN).

A NAS typically consists of:

* One or more hard drives (HDDs/SSDs)
* A lightweight operating system optimized for storage
* Connectivity to your router or switch (Ethernet/Wi-Fi)

With NAS, all devices on the same network (computers, laptops, phones, smart TVs) can access and use the shared storage. Think of it as a **personal Google Drive or Dropbox**, but running locally in your home or office.

---

## Where NAS is Used

* **Home networks:** Centralized storage for photos, music, movies, and backups.
* **Small offices:** Easy file sharing and collaboration among team members.
* **Media servers:** Stream music and video content to TVs, phones, or computers.
* **Application hosting:** Run lightweight applications like Plex, Nextcloud, torrent clients, or Docker containers.
* **Backup solution:** Automatic backups for PCs and mobile devices.

---

## Types of NAS

NAS systems can vary depending on their design and intended use. The main types include:

### 1. **Consumer/Home NAS**

* Designed for home users with simple needs.
* Usually comes as a 1–4 drive system.
* Commonly used for storing movies, photos, music, and personal backups.
* Examples: Synology DS220j, QNAP TS-230.

### 2. **Small Business NAS**

* 2–8 drive bays, more powerful CPUs, more RAM.
* Used for file sharing among employees, light virtualization, and basic backup solutions.
* Supports RAID for redundancy.
* Examples: Synology DS920+, QNAP TS-453D.

### 3. **Enterprise NAS**

* High-performance, large-scale systems with dozens of drive bays.
* Designed for massive data storage, high availability, and enterprise backups.
* Integrated with Active Directory, snapshotting, replication, and high-speed networking (10GbE+).
* Examples: NetApp FAS, Dell EMC Isilon.

### 4. **DIY NAS (Repurposed PCs/Laptops)**

* Built from old hardware using free NAS OS (TrueNAS, OpenMediaVault, UnRAID).
* Perfect for enthusiasts, developers, and cost-saving setups.
* Provides flexibility to experiment with applications and services.

---

## How NAS Works (Deep Dive)

At a high level, NAS is a **mini-server dedicated to file storage**. Let’s break it down:

### 1. **Storage Layer**

* NAS uses one or more drives formatted into storage pools.
* Often configured with **RAID** (Redundant Array of Independent Disks) for redundancy and performance.

  * **RAID 0:** Performance, no redundancy.
  * **RAID 1:** Mirroring, good redundancy.
  * **RAID 5/6:** Balance of storage efficiency and redundancy.
  * **RAID 10:** Performance + redundancy.

### 2. **Operating System Layer**

* NAS runs a special OS optimized for storage (TrueNAS, OMV, Synology DSM).
* It handles:

  * **File system management** (ZFS, EXT4, Btrfs).
  * **User authentication** (local accounts, Active Directory integration).
  * **Network protocols** (SMB, NFS, AFP, iSCSI).

### 3. **Networking Layer**

* NAS connects to the local network via Ethernet (preferred) or Wi-Fi.
* Devices communicate with NAS using network protocols:

  * **SMB/CIFS:** Common for Windows.
  * **NFS:** Common for Linux/Unix.
  * **AFP:** Legacy for macOS.
  * **iSCSI:** Block-level access, used in enterprise setups.

### 4. **Client Access Layer**

* Users access NAS like a shared folder or mapped drive.
* Permissions and quotas ensure security.
* Multiple users/devices can read and write simultaneously.

### 5. **Application Services Layer**

* Beyond storage, NAS can run apps:

  * **Media streaming (Plex, Jellyfin)**.
  * **Private cloud (Nextcloud, OwnCloud)**.
  * **Download servers (Transmission, qBittorrent)**.
  * **Network services (DNS, DHCP, VPN, Pi-hole)**.

### Workflow Example

1. A file is uploaded from a PC via SMB to the NAS.
2. NAS OS writes the file to the storage pool, handling redundancy.
3. Another device (phone, laptop, TV) requests the file.
4. NAS OS authenticates the user and streams the file back.

In essence, NAS works as a **file system over the network** — with a dedicated OS that ensures storage reliability, secure access, and service integration.

---

## Analogy

NAS can be thought of as a dedicated **file server**. Instead of copying files onto USB drives and moving them around manually, you can:

1. Upload files to the NAS from one device.
2. Access those same files from another device on the same Wi-Fi or Ethernet network.

This means no repeated plugging and unplugging of drives, no dependency on third-party cloud services, and centralized access.

---

## Comparison with Amazon S3

### Similarities

* **Central storage hub:** Both allow uploading and downloading files.
* **Multiple clients:** Many devices can access data at the same time.
* **Application integration:** Both can support apps or services that read/write data.

### Differences

| Feature     | NAS                                         | S3                                       |
| ----------- | ------------------------------------------- | ---------------------------------------- |
| Location    | On-premises (home/office) hardware          | AWS Cloud (remote data centers)          |
| Access      | LAN (Wi-Fi/Ethernet), optional remote setup | Internet-based, globally accessible      |
| Scalability | Limited by local hardware capacity          | Virtually unlimited                      |
| Cost        | One-time hardware, electricity              | Pay per GB + data transfer               |
| Performance | Very fast on local LAN (100 MB/s or more)   | Depends on internet speed and AWS region |

In simple terms, NAS is your **private mini-S3**, primarily for local or controlled access.

---

## Use Case: Converting a Windows Machine into a NAS

### Why Use a Windows Machine?

Old Windows desktops or laptops are ideal for NAS because they already include CPU, RAM, disk storage, and network hardware. A laptop even provides a built-in battery, acting as a mini UPS.

### Step 1: Choose Free NAS Software

Options include:

* **TrueNAS CORE** – robust and feature-rich.
* **OpenMediaVault (OMV)** – lightweight, Debian-based, great for beginners.
* **Ubuntu/Debian with Samba/NFS** – more manual but flexible.

For an older Windows machine, **OpenMediaVault** is recommended due to simplicity and efficiency.

### Step 2: Prepare Installation

1. Backup existing files.
2. Download OMV ISO.
3. Create a bootable USB using Rufus or BalenaEtcher.
4. Boot from USB and install OMV.

### Step 3: Configure NAS

1. Connect the machine to your router (Ethernet preferred).
2. Access OMV via another computer’s browser (`http://<NAS-IP>`).
3. Login and configure storage volumes.
4. Create shared folders.
5. Enable SMB/CIFS (Windows sharing) or NFS (Linux sharing).

### Step 4: Access Files

* **Windows PC:** `Win + R` → `\\<NAS-IP>`
* **Linux:** `mount -t nfs <NAS-IP>:/folder /mnt/nas`
* **Mobile:** Apps like CX File Explorer or VLC connect via SMB.
* **Smart TV:** Access directly via SMB/NFS or through media server apps.

### Step 5: Extend Functionality (Optional)

* Add Docker containers for Nextcloud, Plex, or Pi-hole.
* Attach USB drives for more storage.
* Enable remote access with VPN or port forwarding.

---

## Limitations

* **Scalability:** Limited by machine’s hardware.
* **Performance:** Wi-Fi can be slower than Ethernet for large file transfers.
* **Redundancy:** Unless configured with RAID or backups, drive failures risk data loss.
* **Remote Access Setup:** Needs extra configuration for internet access.

---

## FAQ (Common Questions and Answers)

**Q1: What is NAS?**
A storage device connected to a network that lets multiple devices upload, download, and share files.

**Q2: Where is NAS used?**
In homes, offices, and as media servers or application hosts.

**Q3: How does NAS differ from using an external hard drive?**
An external drive connects to one device at a time, while NAS is accessible by all devices on a network.

**Q4: How does NAS compare with S3?**
NAS is local, one-time cost, and fast in LAN. S3 is cloud-based, scalable, but internet-dependent and pay-as-you-go.

**Q5: Can I convert a Windows machine into a NAS for free?**
Yes, by installing free software like OpenMediaVault, TrueNAS, or using Linux with Samba/NFS.

**Q6: Do I need Ethernet?**
Ethernet is recommended for speed and stability, but Wi-Fi works for general use.

**Q7: Can I run apps on a NAS?**
Yes, many NAS OS support Docker or plugins to run apps like Plex, Nextcloud, or torrent clients.

**Q8: Can I access NAS remotely like S3?**
Yes, but it requires additional setup (VPN, dynamic DNS, or port forwarding).

---

## Summary

A NAS transforms a regular Windows machine into a **centralized storage and application hub**, accessible over the network. It eliminates the need for USB transfers, supports streaming, and can even host small applications. While not infinitely scalable like S3, it provides a cost-free, private, and high-performance alternative for personal and small office use.
