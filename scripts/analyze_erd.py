import json
from collections import defaultdict

data = json.load(open('db/diseño.erd'))
print("Keys:", list(data.keys()))
if 'tableModels' in data:
    print("Table array found.")
if 'tableViewModels' in data:
    print("TableView array found. Example:", list(data['tableViewModels'][0].keys()))
if 'columnModels' in data:
    print("Column array found. Example:", list(data['columnModels'][0].keys()))
if 'relationshipModels' in data:
    print("Relationship array found. length:", len(data['relationshipModels']))
    print("Example:", list(data['relationshipModels'][0].keys()))
