export const mockUser = { id: 1, name: 'Gomathi', email: 'g@gmail.com' };

export const mockGroups = [
  { id: 1, name: 'Kodai Trip',       memberCount: 3 },
  { id: 2, name: 'Hostel Room 204',  memberCount: 4 },
  { id: 3, name: 'Project Team',     memberCount: 3 },
];

export const mockMembers = [
  { id: 1, name: 'Pratap'  },
  { id: 2, name: 'Arjun'   },
  { id: 3, name: 'Karthik' },
];

export const mockExpenses = [
  { id:1, description:'Hotel',     amount:900, category:'Hotel',   paidBy:'Pratap',  createdAt:'2026-04-01' },
  { id:2, description:'Food',      amount:600, category:'Food',    paidBy:'Arjun',   createdAt:'2026-04-02' },
  { id:3, description:'Bus Ticket',amount:300, category:'Travel',  paidBy:'Karthik', createdAt:'2026-04-03' },
];

export const mockBalances = [
  { userId:1, name:'Pratap',  netBalance:  100 },
  { userId:2, name:'Arjun',   netBalance:  100 },
  { userId:3, name:'Karthik', netBalance: -200 },
];

export const mockSettlements = [
  { from:'Karthik', to:'Pratap', amount:100 },
  { from:'Karthik', to:'Arjun',  amount:100 },
];
